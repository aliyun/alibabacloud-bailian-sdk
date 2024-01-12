###### 阿里云百炼langchain集成 #####

##### 调用示例 #####

```
    def test_bailian_llm():
        """ 测试基础llm功能 """

        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")
        app_id = os.environ.get("APP_ID")

        llm = Bailian(access_key_id=access_key_id,
                      access_key_secret=access_key_secret,
                      agent_key=agent_key,
                      app_id=app_id)
        out = llm("1+1=?")
        print(out)

    def test_conversation_chain():
        """ 测试包含memory的chain """

        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")
        app_id = os.environ.get("APP_ID")

        llm = Bailian(access_key_id=access_key_id,
                      access_key_secret=access_key_secret,
                      agent_key=agent_key,
                      app_id=app_id)

        memory = ConversationBufferMemory()
        conversant_chain = ConversationChain(memory=memory, llm=llm,
                                             verbose=True)

        conversant_chain.run("我想明天去北京")
        out = conversant_chain.run("那边有什么旅游景点吗")

        print(out)

    def test_conversant_memory():
        """ 测试包含memory的conversant chain """

        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")
        app_id = os.environ.get("APP_ID")

        llm = Bailian(access_key_id=access_key_id,
                      access_key_secret=access_key_secret,
                      agent_key=agent_key,
                      app_id=app_id)

        memory = ConversationBufferMemory()
        conversant_chain = ConversationChain(memory=memory, llm=llm,
                                             verbose=True)

        conversant_chain.run("我想明天去北京")
        out = conversant_chain.run("那边有什么旅游景点吗")

        print(out)

    def test_conversant_bailian_session():
        """ 测试基于broadscope bailian的session id保存上下文 """

        access_key_id = os.environ.get("ACCESS_KEY_ID")
        access_key_secret = os.environ.get("ACCESS_KEY_SECRET")
        agent_key = os.environ.get("AGENT_KEY")
        app_id = os.environ.get("APP_ID")

        llm = Bailian(access_key_id=access_key_id,
                      access_key_secret=access_key_secret,
                      agent_key=agent_key,
                      app_id=app_id)

        session_id = str(uuid.uuid4()).replace('-', '')
        out = llm("我想明天去新疆", session_id=session_id)
        print(out)

        out = llm("那边有什么好玩的地方吗", session_id=session_id)
        print(out)
```