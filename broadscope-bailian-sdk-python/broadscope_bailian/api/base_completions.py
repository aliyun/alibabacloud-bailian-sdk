import json
import threading
import uuid
from typing import Dict, Any, List, Union, Tuple

import requests

import broadscope_bailian
from broadscope_bailian.api.models import ChatRequestQaMessage
from broadscope_bailian.api.models import ChatRequestMessage

_thread_context = threading.local()


class CompletionsRequestError(Exception):
    def __init__(self,
                 message=None,
                 status_code=None,
                 response_body=None
                 ):
        super(CompletionsRequestError, self).__init__(message)

        self.message = message
        self.status_code = status_code
        self.response_body = response_body

    def __str__(self):
        return "%s: status_code=%s, response_body=%s" % (
            self.message,
            self.status_code,
            self.response_body
        )

    def __repr__(self):
        return "(message=%r, status_code=%r, response_body=%r)" % (
            self.message,
            self.status_code,
            self.response_body
        )


class BaseCompletions:
    """ 调用百炼进行文本生成 """

    def __init__(self, token=None, endpoint=None):
        self.token = token
        self.endpoint = endpoint

    @classmethod
    def call(cls, *args, **kwargs) -> Any:
        raise NotImplementedError()

    def reqeust(self, app_id: str,
                prompt: str,
                request_id: str = None,
                session_id: str = None,
                history: List[ChatRequestQaMessage] = None,
                messages: List[ChatRequestMessage] = None,
                top_p: float = 0.0,
                stream: bool = False,
                top_k: int = None,
                seed: int = None,
                use_raw_prompt: bool = None,
                temperature: float = None,
                max_tokens: int = None,
                result_format: str = None,
                stop: List[str] = None,
                incremental_output: bool = None,
                doc_reference_type: str = None,
                doc_tag_ids: List[int] = None,
                doc_tag_codes: List[str] = None,
                biz_params: Dict = None,
                has_thoughts: bool = False,
                timeout: Union[float, Tuple[float, float]] = None):

        self.validate(app_id=app_id, prompt=prompt, messages=messages)

        headers = dict()
        headers["Content-Type"] = "application/json;charset=UTF-8"
        headers["Authorization"] = "Bearer %s" % self.token

        if stream:
            headers["Accept"] = "text/event-stream"

        if request_id is None:
            uuid_obj = uuid.uuid4()
            request_id = str(uuid_obj).replace('-', '')

        parameters = {}
        if top_k is not None:
            parameters["TopK"] = top_k
        if seed is not None:
            parameters["Seed"] = seed
        if use_raw_prompt is not None:
            parameters["UseRawPrompt"] = use_raw_prompt
        if temperature is not None:
            parameters["Temperature"] = temperature
        if max_tokens is not None:
            parameters["MaxTokens"] = max_tokens
        if result_format is not None:
            parameters["ResultFormat"] = result_format
        if stop is not None:
            parameters["Stop"] = stop
        if incremental_output is not None:
            parameters["IncrementalOutput"] = incremental_output

        data = {
            "RequestId": request_id,
            "SessionId": session_id,
            "AppId": app_id,
            "Prompt": prompt,
            "TopP": top_p,
            "Stream": stream,
            "HasThoughts": has_thoughts,
            "BizParams": biz_params,
            "DocReferenceType": doc_reference_type,
            "Parameters": parameters,
            "DocTagIds": doc_tag_ids,
            "DocTagCodes": doc_tag_codes,
        }

        if history is not None:
            new_history = []
            for v in history:
                new_history.append({"User": v.get("user"), "Bot": v.get("bot")})

            data["History"] = new_history

        if messages is not None:
            new_messages = []
            for m in messages:
                new_messages.append({"Role": m.get("role"), "Content": m.get("content")})

            data["Messages"] = new_messages

        url = "%s%s" % (self.endpoint, "/v2/app/completions")
        session = self.__get_session()
        resp = session.request("POST",
                               url=url,
                               headers=headers,
                               data=json.dumps(data),
                               stream=stream,
                               timeout=timeout)

        if not resp.ok:
            raise CompletionsRequestError("completion request error", resp.status_code, resp.text)

        return resp

    @staticmethod
    def __get_session() -> requests.Session:
        if not hasattr(_thread_context, "session"):
            _thread_context.session = requests.Session()

        return _thread_context.session

    def validate(self, app_id: str,
                 prompt: str, messages: List[dict]):
        if self.token is None:
            self.token = broadscope_bailian.api_key

        if broadscope_bailian.api_base is None:
            broadscope_bailian.api_base = "https://bailian.aliyuncs.com"

        if self.endpoint is None:
            self.endpoint = broadscope_bailian.api_base

        if self.token is None:
            raise ValueError("you need to set token before calling api")

        if app_id is None or app_id == '':
            raise ValueError("app id is required")

        if (prompt is None or prompt == '') and (messages is None or len(messages) == 0):
            raise ValueError("prompt or message is required")

    @staticmethod
    def get_param(key, cls_type, **kwargs):
        val = kwargs.get(key, None)
        if val is None:
            return None

        if not isinstance(val, cls_type):
            raise TypeError("%s is not instance of %s" % (key, cls_type))

        return val
