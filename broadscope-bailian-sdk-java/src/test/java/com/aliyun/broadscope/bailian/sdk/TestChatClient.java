/*
 * All rights Reserved, Designed By Alibaba Group Inc.
 * Copyright: Copyright(C) 1999-2023
 * Company  : Alibaba Group Inc.
 */
package com.aliyun.broadscope.bailian.sdk;

import com.aliyun.bailian20230601.Client;
import com.aliyun.bailian20230601.models.*;
import com.aliyun.broadscope.bailian.sdk.consts.ConfigConsts;
import com.aliyun.broadscope.bailian.sdk.consts.DocReferenceTypeEnum;
import com.aliyun.broadscope.bailian.sdk.consts.EmbeddingTextTypeEnum;
import com.aliyun.broadscope.bailian.sdk.models.AccessToken;
import com.aliyun.broadscope.bailian.sdk.models.BaiLianConfig;
import com.aliyun.broadscope.bailian.sdk.models.CompletionsRequest;
import com.aliyun.broadscope.bailian.sdk.models.CompletionsResponse;
import com.aliyun.broadscope.bailian.sdk.utils.UUIDGenerator;
import com.aliyun.teaopenapi.models.Config;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

public class TestChatClient {
    public static void main(String[] args) {
        testCompletions();
    }

    public static void testCompletions() {
        String accessKeyId = "******";
        String accessKeySecret = "******";
        String agentKey = "******";

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        AccessToken accessToken = accessTokenClient.createToken();
        //TODO token过期时间为24小时, 应用侧需要缓存token和过期时间, 过期间重新生成token

        BaiLianConfig config = new BaiLianConfig()
                .setApiKey(accessToken.getToken());

        String appId = "******";
        String prompt = "今天的天气怎么样";
        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setPrompt(prompt);

/*
        List<CompletionsRequest.ChatQaPair> history = new ArrayList<>();
        CompletionsRequest.ChatQaPair chatQaPair = new CompletionsRequest.ChatQaPair("我想去北京", "北京的天气很不错");
        CompletionsRequest.ChatQaPair chatQaPair2 = new CompletionsRequest.ChatQaPair("北京有哪些景点", "北京有故宫、长城等");
        history.add(chatQaPair);
        history.add(chatQaPair2);
        request.setHistory(history);
*/

        ApplicationClient client = new ApplicationClient(config);
        CompletionsResponse response = client.completions(request);
        System.out.println(response);
        System.exit(0);
    }

    public static void testCompletionsWithStream() {
        String accessKeyId = "******";
        String accessKeySecret = "******";
        String agentKey = "******";

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        AccessToken accessToken = accessTokenClient.createToken();
        //TODO token过期时间为24小时, 应用侧需要缓存token和过期时间, 过期间重新生成token

        BaiLianConfig config = new BaiLianConfig()
                .setApiKey(accessToken.getToken());

        String appId = "******";
        String prompt = "FreeSwitch支持哪些操作系统？";
        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setPrompt(prompt)
                .setDocReferenceType(DocReferenceTypeEnum.SIMPLE.getType());

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
                System.out.println("onEvent, data=" + response);
            }

            @Override
            public void onFailure(@Nullable Throwable t, int code, String body) {
                String errMsg = t == null ? "" : t.getMessage();
                System.out.println("onFailure, code=" + code + ", body=" + body + ", err: " + errMsg);
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.exit(0);
    }

    public static void testStreamCompletionsWithSessionId() {
        String accessKeyId = "******";
        String accessKeySecret = "******";
        String agentKey = "******";

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        AccessToken accessToken = accessTokenClient.createToken();
        //TODO token过期时间为24小时, 应用侧需要缓存token和过期时间, 过期间重新生成token

        String appId = "******";
        BaiLianConfig config = new BaiLianConfig()
                .setApiKey(accessToken.getToken());

        String sessionId = UUIDGenerator.generate();

        String prompt = "我要去西安";
        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setPrompt(prompt)
                .setSessionId(sessionId);

        ApplicationClient client = new ApplicationClient(config);
        CompletionsResponse response = client.completions(request);
        System.out.println(response);

        prompt = "帮我定一下酒店";
        CompletionsRequest request2 = new CompletionsRequest()
                .setAppId(appId)
                .setPrompt(prompt)
                .setHasThoughts(true)
                .setSessionId(sessionId);

        ApplicationClient client2 = new ApplicationClient(config);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        client2.streamCompletions(request2, new ApplicationClient.StreamEventListener() {
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
                System.out.println("onEvent, data=" + response);
            }

            @Override
            public void onFailure(@Nullable Throwable t, int code, String body) {
                String errMsg = t == null ? "" : t.getMessage();
                System.out.println("onFailure, code=" + code + ", body=" + body + ", err: " + errMsg);
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void testCreateTextEmbeddings() {
        String accessKeyId = "******";
        String accessKeySecret = "******";
        String agentKey = "******";

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

                System.out.println("textIndex: " + textIndex + ", embedding: " + embeddingsArray);
            }
        } catch (BaiLianSdkException e) {
            throw e;
        } catch (Exception e) {
            throw new BaiLianSdkException(e);
        }
    }
}
