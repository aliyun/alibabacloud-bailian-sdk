import time
from typing import Tuple

from alibabacloud_bailian20230601.client import Client
from alibabacloud_bailian20230601.models import CreateTokenRequest
from alibabacloud_tea_openapi.models import Config

import broadscope_bailian


class AccessToken:
    def __init__(self, token: str, expired_time: int):
        self.token = token
        self.expired_time = expired_time


class AccessTokenClient:
    def __init__(self, access_key_id: str,
                 access_key_secret: str,
                 agent_key: str = None,
                 endpoint: str = None):
        if endpoint is None or endpoint == "":
            endpoint = broadscope_bailian.pop_endpoint

        self.config = Config(access_key_id=access_key_id,
                             access_key_secret=access_key_secret,
                             endpoint=endpoint)
        self.agent_key = agent_key
        self.access_token = None

    def create_token(self, agent_key) -> Tuple[str, int]:
        """
        create access token for access SFM API

        :param agent_key: agent key of SFM
        :return: token and expired time with unix format
        """

        client = Client(config=self.config)

        token_response = client.create_token(CreateTokenRequest(agent_key=agent_key))
        if token_response.status_code != 200 or token_response.body is None:
            raise RuntimeError("create token error, code=%d" % token_response.status_code)

        token_body = token_response.body
        if not token_body.success:
            request_id = token_body.request_id
            if not request_id:
                request_id = token_response.headers.get("x-acs-request-id")
            raise RuntimeError("create token error, code=%s, message=%s RequestId: %s"
                               % (token_body.code, token_body.message, request_id))

        return token_body.data.token, token_body.data.expired_time

    def get_token(self):
        """ get token if not created or expired """

        timestamp = int(time.time())
        if self.access_token is None or (self.access_token.expired_time - 600) < timestamp:
            token, expired_time = self.create_token(self.agent_key)
            self.access_token = AccessToken(token, expired_time)

        return self.access_token.token
