from typing import Dict


class BaseModel:
    def from_dict(self, data: Dict = None):
        pass

    def to_dict(self):
        pass


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
