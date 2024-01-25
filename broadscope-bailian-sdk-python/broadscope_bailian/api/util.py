#!/usr/bin/env python3
# -*-coding:utf-8 -*-

"""
@File    :   util.py
@Date    :   2024-01-22
@Author  :   yuanci.ytb
@Version :   1.0.0
@License :   Copyright(C) 1999-2023, All rights Reserved, Designed By Alibaba Group Inc. 
@Desc    :   #TODO
"""
import warnings


def deprecated(func):
    def new_func(*args, **kwargs):
        warnings.simplefilter('always', DeprecationWarning)
        warnings.warn(f"function [{func.__name__}] is deprecated and will be removed in future versions.",
                      category=DeprecationWarning, stacklevel=2)
        warnings.simplefilter('default', DeprecationWarning)
        return func(*args, **kwargs)

    return new_func
