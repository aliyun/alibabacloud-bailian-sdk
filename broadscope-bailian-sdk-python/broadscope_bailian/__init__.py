import os

from broadscope_bailian.api import (
    Completions,
    AccessTokenClient,
    ChatQaMessage,
    ChatRequestQaMessage,
    ChatSystemMessage,
    ChatUserMessage,
    ChatAssistantMessage,
    deprecated
)

api_key = os.environ.get("BROADSCOPE_API_KEY")
api_base = os.environ.get("BROADSCOPE_API_BASE")
pop_endpoint = "bailian.cn-beijing.aliyuncs.com"
api_version = "1.1.8"
