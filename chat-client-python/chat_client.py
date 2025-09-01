#!/usr/bin/env python3
from typing import List
from importlib import metadata
from packaging import version

if version.parse(metadata.version('pydantic')) < version.parse("2.0"):
    print("pydantic 2+ required")
    print("run:")
    print(" python3 -m venv .")
    print(" bin/pip3 install pydantic packaging python-dateutil urllib3 lazy_imports")
    print(" bin/python3 chat_client.py")
    exit(1)

from chat_openapi.exceptions import ApiException
from chat_openapi.configuration import Configuration
from chat_openapi.api_client import ApiClient
from chat_openapi.api.chat_api import ChatApi
from chat_openapi.models.chat_message import ChatMessage
from chat_openapi.models.new_chat_message_request import NewChatMessageRequest
from chat_openapi.models.background_color_enum import BackgroundColorEnum


configuration = Configuration(host="http://localhost:8080")
configuration.debug = False
api_client: ApiClient = ApiClient(configuration)
api_client.user_agent = "MyChat(Python) 1.0"
chat: ChatApi = ChatApi(api_client)

try:
    # get all messages
    print("all messages:")
    messages: List[ChatMessage] = chat.get_all_messages()
    for idx, msg in enumerate(messages):
        print("#%d (author: %s)" % (idx, msg.author), msg.text)

    # create a new message
    print()
    print("new messages:")
    msg_req = NewChatMessageRequest(text="Hello!", text_color="black", background_color=BackgroundColorEnum("lightgray"))
    msg: ChatMessage = chat.create_message(msg_req, author="Joe")
    print("(author: %s)" % msg.author, msg.text)

    # handling exception
    print()
    print("expected exception for non-existent message id:")
    msg = chat.get_message("-1")

except ApiException as e:
    print("Exception: %s\n" % e)
