###### 通义百炼python SDK #####


##### 文本生成 #####

```
def test_completions():
    access_key_id = "******"
    access_key_secret = "******"
    agent_key = "******"

    client = AccessTokenClient(access_key_id=access_key_id, access_key_secret=access_key_secret)
    token, expired_time = client.create_token(agent_key=agent_key)
    # TODO 应用侧需要自己缓存token和过期时间, 过期前重新生成

    broadscope_bailian.api_key = token

    app_id = "******"
    prompt = "帮我查询下酒店"

    resp = broadscope_bailian.Completions().call(app_id=app_id, prompt=prompt)
    print(resp)
```

##### 流式文本生成 #####

```
    access_key_id = "******"
    access_key_secret = "******"
    agent_key = "******"

    client = AccessTokenClient(access_key_id=access_key_id, access_key_secret=access_key_secret)
    token, expired_time = client.create_token(agent_key=agent_key)
    # TODO 应用侧需要自己缓存token和过期时间, 过期前重新生成

    broadscope_bailian.api_key = token
    app_id = "******"
    prompt = "帮我查询酒店"

    resp = broadscope_bailian.Completions().call(app_id=app_id, prompt=prompt, stream=True, has_thoughts=True)
    for line in resp:
        now = datetime.now()
        print("%s: %s" % (now, line), end="\n", flush=True)
```