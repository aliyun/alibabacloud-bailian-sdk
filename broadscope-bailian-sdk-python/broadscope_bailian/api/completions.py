import json
from typing import Dict, Optional, Iterator, List, Union, Tuple

from broadscope_bailian.api import BaseCompletions
from broadscope_bailian.api.models import ChatQaMessage


class Completions(BaseCompletions):
    """流式文本生成"""

    def call(self, app_id: str,
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
        resp = self.reqeust(app_id=app_id,
                            prompt=prompt,
                            request_id=request_id,
                            session_id=session_id,
                            history=history,
                            top_p=top_p,
                            biz_params=biz_params,
                            has_thoughts=has_thoughts,
                            stream=stream,
                            doc_reference_type=doc_reference_type,
                            top_k=top_k,
                            seed=seed,
                            use_raw_prompt=use_raw_prompt,
                            doc_tag_ids=doc_tag_ids,
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
