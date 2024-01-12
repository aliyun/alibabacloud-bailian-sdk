import logging
from typing import Any, Optional, Dict, List

import broadscope_bailian
from broadscope_bailian import AccessTokenClient
from langchain.callbacks.manager import CallbackManagerForLLMRun
from langchain.llms.base import LLM
from langchain.llms.utils import enforce_stop_tokens
from langchain_core.pydantic_v1 import root_validator

logger = logging.getLogger(__name__)

bailian_access_client: AccessTokenClient = None


class Bailian(LLM):
    """Wrapper of Broadscope Bailian LLM for langchain.

    To use, you should have the `` broadscope_bailian `` python packet installed, and then
    pass the parameters to constructor before calling functions.

    Example:
        .. code-block:: python

            from broadscope_bailian_llm import BroadscopeBailian
            llm = BroadscopeBailian()
            llm("1+1=?")
    """

    client: Any

    access_key_id: str
    """ access key of aliyun account"""

    access_key_secret: str
    """ access key secret of aliyun account """

    agent_key: str
    """ agent key for broad scope business scope """

    app_id: str
    """ id of broadscope bailian application """
    top_p: Optional[float] = None
    """Total probability mass of tokens to consider at each step."""
    streaming: bool = False
    """Whether to stream the results or not."""

    def __call__(self, *args, **kwargs):
        self._init()
        return super().__call__(*args, **kwargs)

    def _init(self):
        global bailian_access_client
        if bailian_access_client is None:
            bailian_access_client = AccessTokenClient(access_key_id=self.access_key_id,
                                                      access_key_secret=self.access_key_secret,
                                                      agent_key=self.agent_key)

    @property
    def _llm_type(self) -> str:
        """Return type of llm."""
        return "broadscope-bailian"

    @root_validator()
    def validate_environment(cls, values: Dict) -> Dict:
        """ Validate input params """

        # Skip creating new client if passed in constructor
        if values["client"] is not None:
            return values

        try:
            import broadscope_bailian

        except ImportError:
            raise ModuleNotFoundError(
                "Could not import broadscope_bailian python package. "
                "Please install it with `pip install broadscope_bailian`."
            )

        return values

    def _call(
            self,
            prompt: str,
            stop: Optional[List[str]] = None,
            run_manager: Optional[CallbackManagerForLLMRun] = None,
            **kwargs: Any,
    ) -> str:
        """Call out to broadscope bailian text generation service.

        """
        self._init()
        global bailian_access_client
        token = bailian_access_client.get_token()
        self.client = broadscope_bailian.Completions(token=token)

        try:
            session_id = kwargs.get("session_id")
            response = self.client.call(app_id=self.app_id, prompt=prompt, stream=self.streaming, session_id=session_id)
            if not response.get("Success"):
                raise RuntimeError(response.get("Message"))

            text = response.get("Data", {}).get("Text")

        except Exception as e:
            raise RuntimeError(f"Error raised by broadscope service: {e}")

        if stop is not None:
            text = enforce_stop_tokens(text, stop)

        return text
