/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk;

import com.aliyun.bailian20230601.Client;
import com.aliyun.bailian20230601.models.CreateTextEmbeddingsRequest;
import com.aliyun.bailian20230601.models.CreateTextEmbeddingsResponse;
import com.aliyun.bailian20230601.models.CreateTextEmbeddingsResponseBody;
import com.aliyun.broadscope.bailian.sdk.consts.ConfigConsts;
import com.aliyun.broadscope.bailian.sdk.consts.EmbeddingTextTypeEnum;
import com.aliyun.teaopenapi.models.Config;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Title embedding tests.<br>
 * Description embedding tests.<br>
 * Created at 2024-01-22 11:42
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class TextEmbeddingTest {
    @Test
    public void testCreateTextEmbeddings() {
        String accessKeyId = System.getenv("ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ACCESS_KEY_SECRET");

        String agentKey = System.getenv("AGENT_KEY");

        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint(ConfigConsts.POP_ENDPOINT);

        try {
            Client client = new Client(config);

            List<String> input = new ArrayList<>();
            input.add("今天天气怎么样");

            CreateTextEmbeddingsRequest request = new CreateTextEmbeddingsRequest()
                    .setAgentKey(agentKey)
                    .setInput(input)
                    .setTextType(EmbeddingTextTypeEnum.DOCUMENT.getType());

            CreateTextEmbeddingsResponse response = client.createTextEmbeddings(request);
            CreateTextEmbeddingsResponseBody body = response.getBody();
            if (body == null || !body.success) {
                String error = body == null ? "create token error" : body.message;
                throw new BaiLianSdkException(error);
            }

            CreateTextEmbeddingsResponseBody.CreateTextEmbeddingsResponseBodyData data = body.getData();
            List<CreateTextEmbeddingsResponseBody.CreateTextEmbeddingsResponseBodyDataEmbeddings> embeddings = data.getEmbeddings();
            for (CreateTextEmbeddingsResponseBody.CreateTextEmbeddingsResponseBodyDataEmbeddings embedding : embeddings) {
                Integer textIndex = embedding.getTextIndex();
                List<Double> embeddingsArray = embedding.getEmbedding();

                System.out.printf("textIndex: %d, embedding: %s\n", textIndex, embeddingsArray);
            }
        } catch (BaiLianSdkException e) {
            throw e;
        } catch (Exception e) {
            throw new BaiLianSdkException(e);
        }
    }
}
