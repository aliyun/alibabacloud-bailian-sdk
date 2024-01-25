#!/usr/bin/env python3
# -*-coding:utf-8 -*-

"""
@File    :   test_embeddings.py
@Date    :   2024-01-22
@Author  :   yuanci.ytb
@Version :   1.0.0
@License :   Copyright(C) 1999-2023, All rights Reserved, Designed By Alibaba Group Inc. 
@Desc    :   embeddings test
"""
import os
import unittest

from alibabacloud_bailian20230601.client import Client
from alibabacloud_bailian20230601.models import CreateTextEmbeddingsRequest
from alibabacloud_tea_openapi.models import Config

import broadscope_bailian


class TextEmbeddingTest(unittest.TestCase):
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
