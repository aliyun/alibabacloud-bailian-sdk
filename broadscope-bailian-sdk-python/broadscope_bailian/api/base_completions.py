import json
import threading
import uuid
from typing import Dict, Any, List, Union, Tuple

import requests

import broadscope_bailian
from broadscope_bailian.api.models import ChatQaMessage

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
    """ 调用百联进行文本生成 """

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
                history: List[ChatQaMessage] = None,
                top_p: float = 0.0,
                biz_params: Dict = None,
                has_thoughts: bool = False,
                stream: bool = False,
                doc_reference_type: str = None,
                top_k: int = None,
                seed: int = None,
                use_raw_prompt: bool = None,
                doc_tag_ids: List[int] = None,
                timeout: Union[float, Tuple[float, float]] = None):

        self.validate(app_id=app_id, prompt=prompt)

        headers = dict()
        headers["Content-Type"] = "application/json;charset=UTF-8"
        headers["Authorization"] = "Bearer %s" % self.token

        if stream:
            headers["Accept"] = "text/event-stream"

        if request_id is None:
            uuid_obj = uuid.uuid4()
            request_id = str(uuid_obj).replace('-', '')

        h = None
        if history is not None:
            h = []
            for v in history:
                h.append(v.to_dict())

        parameters = {}
        if top_k is not None:
            parameters["TopK"] = top_k
        if seed is not None:
            parameters["Seed"] = seed
        if use_raw_prompt is not None:
            parameters["UseRawPrompt"] = use_raw_prompt

        data = {
            "RequestId": request_id,
            "SessionId": session_id,
            "History": h,
            "AppId": app_id,
            "Prompt": prompt,
            "TopP": top_p,
            "Stream": stream,
            "HasThoughts": has_thoughts,
            "BizParams": biz_params,
            "DocReferenceType": doc_reference_type,
            "Parameters": parameters,
            "DocTagIds": doc_tag_ids
        }

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
                 prompt: str):
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

        if prompt is None or prompt == '':
            raise ValueError("prompt is required")
