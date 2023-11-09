/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk;

import com.alibaba.fastjson.JSON;
import com.aliyun.bailian20230601.Client;
import com.aliyun.bailian20230601.models.CreateTextEmbeddingsRequest;
import com.aliyun.bailian20230601.models.CreateTextEmbeddingsResponse;
import com.aliyun.bailian20230601.models.CreateTextEmbeddingsResponseBody;
import com.aliyun.broadscope.bailian.sdk.consts.ConfigConsts;
import com.aliyun.broadscope.bailian.sdk.consts.DocReferenceTypeEnum;
import com.aliyun.broadscope.bailian.sdk.consts.EmbeddingTextTypeEnum;
import com.aliyun.broadscope.bailian.sdk.models.BaiLianConfig;
import com.aliyun.broadscope.bailian.sdk.models.CompletionsRequest;
import com.aliyun.broadscope.bailian.sdk.models.CompletionsResponse;
import com.aliyun.broadscope.bailian.sdk.models.ConnectOptions;
import com.aliyun.broadscope.bailian.sdk.utils.UUIDGenerator;
import com.aliyun.teaopenapi.models.Config;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Title 对话客户端测试用例.<br/>
 * Description 对话客户端测试用例.<br/>
 * Created at 2023-06-07 17:10
 *
 * @author yuanci.ytb
 * @version 1.0.0
 * @since jdk8
 */

public class TestApplicationClient {

    @Test
    public void testCompletions() {
        String accessKeyId = System.getenv("ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ACCESS_KEY_SECRET");

        String agentKey = System.getenv("AGENT_KEY");
        String appId = System.getenv("APP_ID");

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        String token = accessTokenClient.getToken();
        BaiLianConfig config = new BaiLianConfig()
                .setApiKey(token);

        String prompt = "帮我生成一篇200字的文章，描述一下春秋战国的政治、军事和经济";
        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setPrompt(prompt);

        ApplicationClient client = new ApplicationClient(config);
        CompletionsResponse response = client.completions(request);

        if (!response.isSuccess()) {
            System.out.printf("failed to create completion, requestId: %s, code: %s, message: %s\n",
                    response.getRequestId(), response.getCode(), response.getMessage());
            return;
        }

        System.out.printf("requestId: %s, text: %s\n", response.getRequestId(), response.getData().getText());
    }

    @Test
    public void testCompletionsWithStream() {
        String accessKeyId = System.getenv("ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ACCESS_KEY_SECRET");

        String agentKey = System.getenv("AGENT_KEY");
        String appId = System.getenv("APP_ID");

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);

        String token = accessTokenClient.getToken();
        BaiLianConfig config = new BaiLianConfig()
                .setApiKey(token);

        String prompt = "帮我生成一篇200字的文章，描述一下春秋战国的政治、军事和经济";
        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setPrompt(prompt);

        ApplicationClient client = new ApplicationClient(config);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        client.streamCompletions(request, new ApplicationClient.StreamEventListener() {
            @Override
            public void onOpen() {
                System.out.println("onOpen");
            }

            @Override
            public void onClosed() {
                System.out.println("onClosed");
                countDownLatch.countDown();
            }

            @Override
            public void onEvent(CompletionsResponse response) {
                if (!response.isSuccess()) {
                    System.out.printf("failed to create completion, requestId: %s, code: %s, message: %s\n",
                            response.getRequestId(), response.getCode(), response.getMessage());
                } else {
                    System.out.printf("requestId: %s, text=%s\n", response.getRequestId(), response.getData().getText());
                }
            }

            @Override
            public void onFailure(@Nullable Throwable t, int code, String body) {
                String errMsg = t == null ? "" : ExceptionUtils.getStackTrace(t);
                System.out.printf("onFailure, code: %d, body: %s, err: %s\n", code, body, errMsg);
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCompletionsWithParams() {
        String accessKeyId = System.getenv("ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ACCESS_KEY_SECRET");

        String agentKey = System.getenv("AGENT_KEY");
        String appId = System.getenv("APP_ID");

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        String token = accessTokenClient.getToken();
        BaiLianConfig config = new BaiLianConfig()
                .setApiKey(token);

        String prompt = "云南近五年GNP总和是多少";

        CompletionsRequest request = new CompletionsRequest();
        request.setAppId(appId);
        request.setPrompt(prompt);

        //设置模型参数topP的值
        request.setTopP(0.2);

        //开启历史上下文, sessionId需要采用uuid保证唯一性, 后续传入相同sessionId，百炼平台将自动维护历史上下文
        String sessionId = UUIDGenerator.generate();
        request.setSessionId(sessionId);

        //设置历史上下文, 由调用侧维护历史上下文, 如果同时传入sessionId和history, 优先使用调用者管理的对话上下文
        List<CompletionsRequest.ChatQaPair> history = new ArrayList<>();
        CompletionsRequest.ChatQaPair chatQaPair = new CompletionsRequest.ChatQaPair("我想去北京", "北京的天气很不错");
        CompletionsRequest.ChatQaPair chatQaPair2 = new CompletionsRequest.ChatQaPair("北京有哪些景点", "北京有故宫、长城等");
        history.add(chatQaPair);
        history.add(chatQaPair2);
        request.setHistory(history);

        //设置模型参数topK，seed
        CompletionsRequest.Parameter modelParameter = new CompletionsRequest.Parameter()
                .setTopK(50)
                .setSeed(2222)
                .setUseRawPrompt(true);
        request.setParameters(modelParameter);

        //设置文档标签tagId，设置后，文档检索召回时，仅从tagIds对应的文档范围进行召回
        request.setDocTagIds(Arrays.asList(100L, 101L));

        //返回文档检索的文档引用数据
        request.setDocReferenceType(DocReferenceTypeEnum.SIMPLE.getType());

        String sqlSchema = "{" +
                "    sqlInput: {" +
                "      \"synonym_infos\": \"国民生产总值: GNP|Gross National Product\"," +
                "      \"schema_infos\": [" +
                "        {" +
                "          \"columns\": [" +
                "            {" +
                "              \"col_caption\": \"地区\"," +
                "              \"col_name\": \"region\"" +
                "            }," +
                "            {" +
                "              \"col_caption\": \"年份\"," +
                "              \"col_name\": \"year\"" +
                "            }," +
                "            {" +
                "              \"col_caption\": \"国民生产总值\"," +
                "              \"col_name\": \"gross_national_product\"" +
                "            }" +
                "          ]," +
                "          \"table_id\": \"t_gross_national_product_1\"," +
                "          \"table_desc\": \"国民生产总值表\"" +
                "        }" +
                "      ]" +
                "    }" +
                "  }";
        request.setBizParams(JSON.parseObject(sqlSchema));

        ApplicationClient client = new ApplicationClient(config, new ConnectOptions(30000, 60000, 60000));
        CompletionsResponse response = client.completions(request);
        if (!response.isSuccess()) {
            System.out.printf("failed to create completion, requestId: %s, code: %s, message: %s",
                    response.getRequestId(), response.getCode(), response.getMessage());
            return;
        }

        System.out.printf("requestId: %s, text: %s\n", response.getRequestId(), response.getData().getText());
    }

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
                    .setTextType(EmbeddingTextTypeEnum.QUERY.getType());

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
