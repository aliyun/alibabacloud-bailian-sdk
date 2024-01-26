import os
import unittest
import uuid

import broadscope_bailian


class CompletionTest(unittest.TestCase):
    def test_model_completions(self):
        """ 官方大模型调用应用、自训练模型应用示例 """

        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")
        app_id = os.environ.get("APP_ID")

        client = broadscope_bailian.AccessTokenClient(access_key_id=access_key_id, access_key_secret=access_key_secret,
                                                      agent_key=agent_key)
        token = client.get_token()

        resp = broadscope_bailian.Completions(token=token).create(
            app_id=app_id,
            messages=[
                {"role": "system", "content": "你是一名历史学家"},
                {"role": "user", "content": "帮我生成一篇200字的文章，描述一下春秋战国的经济和文化"}
            ],
            result_format="message"
        )

        if not resp.get("Success"):
            print('failed to create completion, request_id: %s, code: %s, message: %s' % (
                resp.get("RequestId"), resp.get("Code"), resp.get("Message")))
            return

        content = resp.get("Data", {}).get("Choices", [])[0].get("Message", {}).get("Content")
        print("request_id: %s, content: %s\n" % (resp.get("RequestId"), content))

    def test_completions_with_params(self):
        """ 官方大模型调用应用、自训练模型应用-其他参数使用示例 """

        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")
        app_id = os.environ.get("APP_ID")

        client = broadscope_bailian.AccessTokenClient(access_key_id=access_key_id, access_key_secret=access_key_secret,
                                                      agent_key=agent_key)
        token = client.get_token()

        # //开启历史上下文, sessionId需要采用uuid保证唯一性, 后续传入相同sessionId，百炼平台将自动维护历史上下文
        session_id = str(uuid.uuid4()).replace("-", "")

        # 设置历史上下文, 由调用侧维护历史上下文, 如果同时传入sessionId和history, 优先使用调用者管理的对话上下文
        messages = [
            {"role": "system", "content": "你是一个旅行专家, 能够帮我们制定旅行计划"},
            {"role": "user", "content": "我想去北京"},
            {"role": "assistant", "content": "北京是一个非常值得去的地方"},
            {"role": "user", "content": "那边有什么推荐的旅游景点"}
        ]

        resp = broadscope_bailian.Completions(token=token).create(
            app_id=app_id,
            messages=messages,
            # 设置模型参数topP的值
            top_p=0.2,
            session_id=session_id,
            # 设置模型参数topK
            top_k=50,
            # 设置模型参数seed
            seed=2222,
            # 设置模型参数temperature
            temperature=0.3,
            # 设置模型参数max tokens
            max_tokens=50,
            # 按message方式返回结果
            result_format="message",
            # 设置停止词
            stop=["景点"],
            # 超时设置, 单位秒
            timeout=30,
        )

        if not resp.get("Success"):
            print('failed to create completion, request_id: %s, code: %s, message: %s' % (
                resp.get("RequestId"), resp.get("Code"), resp.get("Message")))
            return

        content = resp.get("Data", {}).get("Choices", [])[0].get("Message", {}).get("Content")
        print("request_id: %s, content: %s\n" % (resp.get("RequestId"), content))
        if resp.get("Data", "").get("Usage") is not None and len(resp.get("Data", "").get("Usage")) > 0:
            usage = resp.get("Data", "").get("Usage")[0]
            print("input tokens: %d, output tokens: %d" % (usage.get("InputTokens"), usage.get("OutputTokens")))

    def test_stream_completions(self):
        """ 流式响应使用示例 """

        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")
        app_id = os.environ.get("APP_ID")

        client = broadscope_bailian.AccessTokenClient(access_key_id=access_key_id, access_key_secret=access_key_secret,
                                                      agent_key=agent_key)
        token = client.get_token()

        resp = broadscope_bailian.Completions(token=token).create(
            app_id=app_id,
            messages=[
                {"role": "system", "content": "你是一名天文学家, 能够帮助小学生回答宇宙与天文方面的问题"},
                {"role": "user", "content": "宇宙中为什么会存在黑洞"}
            ],
            stream=True,
            # 返回choice message结果
            result_format="message",
            # 开启增量输出模式，后面输出不会包含已经输出的内容
            incremental_output=True
        )

        for result in resp:
            if not result.get("Success"):
                print("failed to create completion, request_id: %s, code: %s, message: %s" %
                      (result.get("RequestId"), result.get("Code"), result.get("Message")))
            else:
                print("%s" % result.get("Data", {}).get("Choices", [])[0].get("Message", {}).get("Content"),
                      end="", flush=True)

    def test_third_model_completions(self):
        """ 三方模型应用示例 """

        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")
        app_id = os.environ.get("APP_ID")

        client = broadscope_bailian.AccessTokenClient(access_key_id=access_key_id, access_key_secret=access_key_secret,
                                                      agent_key=agent_key)
        token = client.get_token()

        chat_history = [
            {"user": "我想去北京", "bot": "北京是一个非常值得去的地方"}
        ]

        prompt = "那边有什么推荐的旅游景点"
        resp = broadscope_bailian.Completions(token=token).create(
            app_id=app_id,
            prompt=prompt,
            history=chat_history,
        )

        if not resp.get("Success"):
            print("failed to create completion, request_id: %s, code: %s, message: %s" %
                  (resp.get("RequestId"), resp.get("Code"), resp.get("Message")))
        else:
            print("request_id: %s, text: %s" % (resp.get("RequestId"), resp.get("Data", {}).get("Text")))
            doc_references = resp.get("Data", {}).get("DocReferences")
            if doc_references is not None and len(doc_references) > 0:
                print("doc ref: %s" % doc_references[0].get("DocName"))

    def test_rag_app_completions(self):
        """ 检索增强应用示例 """

        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")
        app_id = os.environ.get("APP_ID")

        client = broadscope_bailian.AccessTokenClient(access_key_id=access_key_id, access_key_secret=access_key_secret,
                                                      agent_key=agent_key)
        token = client.get_token()

        chat_history = [
            {"user": "API接口如何使用", "bot": "API接口需要传入prompt、app id并通过post方法调用"}
        ]

        prompt = "API接口说明中, TopP参数改如何传递?"

        resp = broadscope_bailian.Completions(token=token).create(
            app_id=app_id,
            prompt=prompt,
            history=chat_history,
            # 返回文档检索的文档引用数据, 传入为simple或indexed
            doc_reference_type="simple",
            # 文档标签code列表
            doc_tag_codes=["471d*******3427", "881f*****0c232"]
        )

        if not resp.get("Success"):
            print("failed to create completion, request_id: %s, code: %s, message: %s" %
                  (resp.get("RequestId"), resp.get("Code"), resp.get("Message")))
        else:
            print("request_id: %s, text: %s\n" % (resp.get("RequestId"), resp.get("Data", {}).get("Text")))
            doc_references = resp.get("Data", {}).get("DocReferences")
            if doc_references is not None and len(doc_references) > 0:
                print("Doc ref: %s" % doc_references[0].get("DocName"))

    def test_flow_app_completions(self):
        """ 插件和流程编排应用使用示例 """

        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")
        app_id = os.environ.get("APP_ID")

        client = broadscope_bailian.AccessTokenClient(access_key_id=access_key_id, access_key_secret=access_key_secret,
                                                      agent_key=agent_key)
        token = client.get_token()

        biz_params = {"userId": "123"}

        prompt = "今天杭州的天气怎么样"
        resp = broadscope_bailian.Completions(token=token).create(
            app_id=app_id,
            prompt=prompt,
            biz_params=biz_params
        )

        if not resp.get("Success"):
            print("failed to create completion, request_id: %s, code: %s, message: %s" %
                  (resp.get("RequestId"), resp.get("Code"), resp.get("Message")))
        else:
            print("request_id: %s, text: %s" % (resp.get("RequestId"), resp.get("Data", {}).get("Text")))

    def test_nl2sql_completions(self):
        """ Nl2SQL应用示例 """

        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")
        app_id = os.environ.get("APP_ID")

        client = broadscope_bailian.AccessTokenClient(access_key_id=access_key_id,
                                                      access_key_secret=access_key_secret,
                                                      agent_key=agent_key)
        token = client.get_token()

        sql_schema = {
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
        prompt = "浙江近五年GNP总和是多少"

        resp = broadscope_bailian.Completions(token=token).create(
            app_id=app_id,
            prompt=prompt,
            # 自然语言转sql调用示例
            biz_params=sql_schema
        )

        if not resp.get("Success"):
            print("failed to create completion, request_id: %s, code: %s, message: %s" %
                  (resp.get("RequestId"), resp.get("Code"), resp.get("Message")))
        else:
            print("request_id: %s, text: %s" % (resp.get("RequestId"), resp.get("Data", "").get("Text")))


if __name__ == '__main__':
    unittest.main()
