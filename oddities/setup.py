from cx_Freeze import setup, Executable


# Setup parameters
setup(
    name="Flask Extractor",
    version="1.0",
    author="IchChefDuNixx",
    executables=[
        Executable('flask_extractor.py')
    ]
)