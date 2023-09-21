import json
import unittest

from alibabacloud_bailian20230601.client import Client
from alibabacloud_bailian20230601.models import CreateTextEmbeddingsRequest
from alibabacloud_tea_openapi.models import Config

import broadscope_bailian
from broadscope_bailian import ChatQaMessage


class CompletionTest(unittest.TestCase):
    def test_completions(self):
        access_key_id = "******"
        access_key_secret = "******"
        agent_key = "******"

        client = broadscope_bailian.AccessTokenClient(access_key_id=access_key_id,
                                                      access_key_secret=access_key_secret,
                                                      agent_key=agent_key)
        token = client.get_token()
        broadscope_bailian.api_key = token

        app_id = "******"
        prompt = "FreeSwitch支持哪些操作系统"

        resp = broadscope_bailian.Completions().call(app_id=app_id, prompt=prompt)
        print(resp)

    def test_completions_history(self):
        access_key_id = "******"
        access_key_secret = "******"
        agent_key = "******"

        client = broadscope_bailian.AccessTokenClient(access_key_id=access_key_id,
                                                      access_key_secret=access_key_secret,
                                                      agent_key=agent_key)
        token = client.get_token()
        broadscope_bailian.api_key = token

        app_id = "******"
        prompt = "帮我查询下酒店"

        # 传入相同的session id, 百炼将自动维护多轮对话上下文, 优先使用调用者管理的对话上下文
        chat_history = [ChatQaMessage("我想去北京", "北京的天气很不错"),
                        ChatQaMessage("背景有哪些景点呢", "背景有故宫、长城等景点")]
        resp = broadscope_bailian.Completions().call(app_id=app_id, prompt=prompt, history=chat_history)
        print(resp)

    def test_stream_completions(self):
        access_key_id = "******"
        access_key_secret = "******"
        agent_key = "******"

        client = broadscope_bailian.AccessTokenClient(access_key_id=access_key_id,
                                                      access_key_secret=access_key_secret,
                                                      agent_key=agent_key)
        token = client.get_token()

        broadscope_bailian.api_key = token
        app_id = "******"
        prompt = "帮我查询酒店"

        resp = broadscope_bailian.Completions().call(app_id=app_id, prompt=prompt, stream=True, has_thoughts=True)
        for line in resp:
            print("%s" % line, end="\n", flush=True)

    def test_create_embeddings(self):
        access_key_id = "******"
        access_key_secret = "******"
        agent_key = "******"

        config = Config(access_key_id=access_key_id,
                        access_key_secret=access_key_secret,
                        endpoint=broadscope_bailian.pop_endpoint)

        client = Client(config=config)

        request = CreateTextEmbeddingsRequest(agent_key=agent_key,
                                              input=["今天天气怎么样", "我想去北京"],
                                              text_type="query")
        response = client.create_text_embeddings(request=request)
        if response.status_code != 200 or response.body is None:
            raise RuntimeError("create token error, code=%d" % response.status_code)

        body = response.body
        if not body.success:
            raise RuntimeError("create token error, code=%s, message=%s" % (body.code, body.message))

        for embedding in body.data.embeddings:
            print("index: %s, embeddings: %s\n" % (embedding.text_index, embedding.embedding))


if __name__ == '__main__':
    unittest.main()
