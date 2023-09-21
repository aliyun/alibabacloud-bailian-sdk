from setuptools import setup, find_packages

PACKAGE = "broadscope_bailian"
VERSION = "1.1.7"

requires = [
    'requests >= 2.20',
    'alibabacloud_bailian20230601 >= 1.1.2'
]

setup(
    name=PACKAGE,
    version=VERSION,
    description='Alibaba Cloud Broadscope BaiLian SDK for Python',
    author='Alibaba Cloud SDK',
    author_email='sdk-team@alibabacloud.com',
    url='https://bailian.console.aliyun.com/',
    keywords=["alibabacloud", "broadscope-bailian"],
    packages=find_packages(exclude=['tests*']),
    platforms="any",
    install_requires=requires,
    python_requires=">=3.7",
    license="MIT License",
    classifiers=[
        'Intended Audience :: Developers',
        'Natural Language :: English',
        'License :: OSI Approved :: MIT License',
        'Programming Language :: Python :: 3.7',
        'Programming Language :: Python :: 3.8',
        'Programming Language :: Python :: 3.9',
        'Programming Language :: Python :: 3.10',
    ],
)
