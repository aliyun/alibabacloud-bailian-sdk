import json
import os
import unittest
import uuid

from alibabacloud_bailian20230601.client import Client
from alibabacloud_bailian20230601.models import CreateTextEmbeddingsRequest
from alibabacloud_tea_openapi.models import Config

import broadscope_bailian
from broadscope_bailian import ChatQaMessage


class CompletionTest(unittest.TestCase):
    def test_completions(self):
        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")
        app_id = os.environ.get("APP_ID")

        client = broadscope_bailian.AccessTokenClient(access_key_id=access_key_id, access_key_secret=access_key_secret,
                                                      agent_key=agent_key)
        token = client.get_token()

        prompt = "帮我生成一篇200字的文章，描述一下春秋战国的政治、军事和经济"
        resp = broadscope_bailian.Completions(token=token).call(app_id=app_id, prompt=prompt)

        if not resp.get("Success"):
            print('failed to create completion, request_id: %s, code: %s, message: %s' % (
                resp.get("RequestId"), resp.get("Code"), resp.get("Message")))
            return

        print("request_id: %s, text: %s" % (resp.get("RequestId"), resp.get("Data", '').get("Text")))

    def test_stream_completions(self):
        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")
        app_id = os.environ.get("APP_ID")

        client = broadscope_bailian.AccessTokenClient(access_key_id=access_key_id, access_key_secret=access_key_secret,
                                                      agent_key=agent_key)
        token = client.get_token()

        prompt = "帮我生成一篇200字的文章，描述一下春秋战国的政治、军事和经济"
        resp = broadscope_bailian.Completions(token=token).call(app_id=app_id, prompt=prompt, stream=True)
        for result in resp:
            if not result.get("Success"):
                print("failed to create completion, request_id: %s, code: %s, message: %s" %
                      (result.get("RequestId"), result.get("Code"), result.get("Message")))
            else:
                print("request_id: %s, text: %s" % (result.get("RequestId"), result.get("Data", '').get("Text")),
                      end="\n", flush=True)

    def test_completions_with_params(self):
        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")
        app_id = os.environ.get("APP_ID")

        client = broadscope_bailian.AccessTokenClient(access_key_id=access_key_id, access_key_secret=access_key_secret,
                                                      agent_key=agent_key)
        token = client.get_token()

        session_id = str(uuid.uuid4()).replace("-", "")

        chat_history = [ChatQaMessage("我想去北京", "北京的天气很不错"),
                        ChatQaMessage("背景有哪些景点呢", "背景有故宫、长城等景点")]

        prompt = "云南近5年GNP总和是多少"
        sql_schema = """
        {
            "sqlInput": {
              "synonym_infos": "国民生产总值: GNP|Gross National Product",
              "schema_infos": [
                {
                  "columns": [
                    {
                      "col_caption": "地区",
                      "col_name": "region"
                    },
                    {
                      "col_caption": "年份",
                      "col_name": "year"
                    },
                    {
                      "col_caption": "国民生产总值",
                      "col_name": "gross_national_product"
                    }
                  ],
                  "table_id": "t_gross_national_product_1",
                  "table_desc": "国民生产总值表"
                }
              ]
            }
          }
        """

        resp = broadscope_bailian.Completions(token=token).call(
            app_id=app_id, prompt=prompt,
            # 设置模型参数topP的值
            top_p=0.2,
            # 设置历史上下文, 由调用侧维护历史上下文, 如果同时传入sessionId和history, 优先使用调用者管理的对话上下文
            session_id=session_id,
            # 设置历史上下文, 由调用侧维护历史上下文, 如果同时传入sessionId和history, 优先使用调用者管理的对话上下文
            history=chat_history,
            # 设置模型参数topK，seed
            top_k=50,
            seed=2222,
            use_raw_prompt=True,
            # 设置文档标签tagId，设置后，文档检索召回时，仅从tagIds对应的文档范围进行召回
            doc_tag_ids=[101, 102],
            # 返回文档检索的文档引用数据, 传入为simple或indexed
            doc_reference_type="simple",
            # 自然语言转sql调用示例
            biz_params=json.loads(sql_schema))

        if not resp.get("Success"):
            print("failed to create completion, request_id: %s, code: %s, message: %s" %
                  (resp.get("RequestId"), resp.get("Code"), resp.get("Message")))
        else:
            print("request_id: %s, text: %s" %(resp.get("RequestId"), resp.get("Data", "").get("Text")))

    def test_create_embeddings(self):
        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")

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
