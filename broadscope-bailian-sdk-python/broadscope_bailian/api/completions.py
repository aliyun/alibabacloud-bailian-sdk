import json
from typing import Dict, Optional, Iterator, List, Union, Tuple, Any

from broadscope_bailian.api import BaseCompletions
from broadscope_bailian.api.models import ChatQaMessage
from broadscope_bailian.api.models import ChatRequestQaMessage
from broadscope_bailian.api.models import ChatRequestMessage
from broadscope_bailian.api.util import deprecated


class Completions(BaseCompletions):
    """文本生成"""

    def create(self,
               app_id: str,
               prompt: str = None,
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
               doc_tag_codes: List[str] = None,
               doc_reference_type: str = None,
               biz_params: Dict = None,
               has_thoughts: bool = False,
               timeout: Union[float, Tuple[float, float]] = None):
        resp = self.reqeust(app_id=app_id,
                            prompt=prompt,
                            request_id=request_id,
                            session_id=session_id,
                            history=history,
                            messages=messages,
                            top_p=top_p,
                            stream=stream,
                            top_k=top_k,
                            seed=seed,
                            use_raw_prompt=use_raw_prompt,
                            temperature=temperature,
                            max_tokens=max_tokens,
                            result_format=result_format,
                            stop=stop,
                            incremental_output=incremental_output,
                            doc_reference_type=doc_reference_type,
                            doc_tag_codes=doc_tag_codes,
                            biz_params=biz_params,
                            has_thoughts=has_thoughts,
                            timeout=timeout)

        if stream:
            return (json.loads(line) for line in self.parse_response_stream(resp.iter_lines()))
        else:
            return json.loads(resp.text)

    @deprecated
    def call(self,
             app_id: str,
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
             timeout: Union[float, Tuple[float, float]] = None,
             **kwargs: Any):

        temperature = kwargs.get("temperature", None)
        max_tokens = kwargs.get("max_tokens", None)
        result_format = kwargs.get("result_format", None)
        stop = kwargs.get("stop", None)
        incremental_output = kwargs.get("incremental_output", None)

        new_history = None
        if history is not None:
            new_history = []
            for h in history:
                new_history.append({"user": h.user, "bot": h.bot})

        resp = self.reqeust(app_id=app_id,
                            prompt=prompt,
                            request_id=request_id,
                            session_id=session_id,
                            history=new_history,
                            top_p=top_p,
                            stream=stream,
                            top_k=top_k,
                            seed=seed,
                            use_raw_prompt=use_raw_prompt,
                            temperature=temperature,
                            max_tokens=max_tokens,
                            result_format=result_format,
                            stop=stop,
                            incremental_output=incremental_output,
                            doc_reference_type=doc_reference_type,
                            doc_tag_ids=doc_tag_ids,
                            biz_params=biz_params,
                            has_thoughts=has_thoughts,
                            timeout=timeout)

        if stream:
            return (json.loads(line) for line in self.parse_response_stream(resp.iter_lines()))
        else:
            return json.loads(resp.text)

    def parse_response_stream(self, resp: Iterator[bytes]) -> Iterator[str]:
        for line in resp:
            _line = self.parse_stream_line(line)
            if _line is not None:
                yield _line

    @staticmethod
    def parse_stream_line(line: bytes) -> Optional[str]:
        if line:
            if line.strip() == b"data: [DONE]":
                return None
            if line.startswith(b"data: "):
                line = line[len(b"data: "):]
                return line.decode("utf-8")
            else:
                return None
        return None
