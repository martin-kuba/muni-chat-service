#!/usr/bin/env python3
from typing import List

from chat_openapi.exceptions import ApiException
from chat_openapi.configuration import Configuration
from chat_openapi.api_client import ApiClient
from chat_openapi.api.chat_api import ChatApi
from chat_openapi.model.chat_message import ChatMessage
from chat_openapi.model.new_chat_message_request import NewChatMessageRequest
from chat_openapi.model.background_color_enum import BackgroundColorEnum


configuration = Configuration(host="http://localhost:8080")
configuration.debug = False
api_client: ApiClient = ApiClient(configuration)
api_client.user_agent = "MyChat(Python) 1.0"
chat: ChatApi = ChatApi(api_client)

try:
    # get all messages
    messages: List[ChatMessage] = chat.get_all_messages()
    for msg in messages:
        print("(%s)" % msg.author, msg.text)

    # create a new message
    msg_req = NewChatMessageRequest(text="Hello!", text_color="black", background_color=BackgroundColorEnum("lightgray"))
    msg: ChatMessage = chat.create_message(msg_req, author="Joe")
    print("(%s)" % msg.author, msg.text)

    # would cause exception
    # chat.get_message("-1")

except ApiException as e:
    print("Exception: %s\n" % e)
