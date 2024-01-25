from typing import Dict, Union

from typing_extensions import TypedDict, Required, Literal

from broadscope_bailian.api.util import deprecated


class BaseModel:
    def from_dict(self, data: Dict = None):
        pass

    def to_dict(self):
        pass


@deprecated
class ChatQaMessage(BaseModel):
    def __init__(self, user, bot):
        self.user = user
        self.bot = bot

    def from_dict(self, data: Dict = None):
        data = data or dict()
        if data.get("User") is not None:
            self.user = data.get("User")
        if data.get("Bot") is not None:
            self.bot = data.get("Bot")

        return self

    def to_dict(self):
        data = dict()
        if self.user is not None:
            data["User"] = self.user
        if self.bot is not None:
            data["Bot"] = self.bot

        return data


class ChatRequestQaMessage(TypedDict):
    user: Required[str]
    """ content of user """

    bot: Required[str]
    """ content of bot """


class ChatSystemMessage(TypedDict):
    role: Required[Literal["system"]]
    """ system role """

    content: str
    """ content of system """


class ChatUserMessage(TypedDict):
    role: Required[Literal["user"]]
    """ user role """

    content: Required[str]
    """ content of user """


class ChatAssistantMessage(TypedDict):
    role: Required[Literal["assistant"]]
    """ user role """

    content: Required[str]
    """ content of user """


ChatRequestMessage = Union[
    ChatSystemMessage,
    ChatUserMessage,
    ChatAssistantMessage
]
