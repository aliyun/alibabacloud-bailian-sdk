from typing import Tuple

from alibabacloud_bailian20230601.client import Client
from alibabacloud_bailian20230601.models import CreateTokenRequest
from alibabacloud_tea_openapi.models import Config

import broadscope_bailian


class AccessTokenClient:
    def __init__(self, access_key_id: str,
                 access_key_secret: str,
                 endpoint: str = None):
        if endpoint is None or endpoint == "":
            endpoint = broadscope_bailian.pop_endpoint

        self.config = Config(access_key_id=access_key_id,
                             access_key_secret=access_key_secret,
                             endpoint=endpoint)

    def create_token(self, agent_key) -> Tuple[str, int]:
        client = Client(config=self.config)

        token_response = client.create_token(CreateTokenRequest(agent_key=agent_key))
        if token_response.status_code != 200 or token_response.body is None:
            raise RuntimeError("create token error, code=%d" % token_response.status_code)

        token_body = token_response.body
        if not token_body.success:
            raise RuntimeError("create token error, code=%s, message=%s" % (token_body.code, token_body.message))

        return token_body.data.token, token_body.data.expired_time
