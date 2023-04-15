from setuptools import setup

setup(
    name='Spellstone Flask Extractor',
    version='1.3',
    author='IchChefDuNixx',
    entry_points={
        'console_scripts': ['flask_extractor = flask_extractor.__main__:not_main']
    }
)