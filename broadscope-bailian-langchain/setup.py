from setuptools import setup, find_packages

PACKAGE = "langchain-bailian"
VERSION = "1.0.0"

requires = [
    'requests >= 2.20',
    'alibabacloud_bailian20230601 >= 1.1.9',
    'langchain >= 0.0.350'
]

setup(
    name=PACKAGE,
    version=VERSION,
    description='Alibaba Cloud BaiLian and Langchain Integration SDK for Python',
    author='Alibaba Cloud SDK',
    author_email='sdk-team@alibabacloud.com',
    url='https://bailian.console.aliyun.com/',
    keywords=["alibabacloud", "langchain-bailian"],
    packages=find_packages(exclude=['tests*']),
    platforms="any",
    install_requires=requires,
    python_requires=">=3.9",
    license="MIT License",
    classifiers=[
        'Intended Audience :: Developers',
        'Natural Language :: English',
        'License :: OSI Approved :: MIT License',
        'Programming Language :: Python :: 3.9',
        'Programming Language :: Python :: 3.10',
        'Programming Language :: Python :: 3.11',
    ],
)
