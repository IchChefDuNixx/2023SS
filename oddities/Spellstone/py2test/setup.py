from distutils.core import setup
import py2exe

setup(console=['flask_extractor.py'],
    options={
        'py2exe': {
            'includes': ["readchar"]
        }
    }
)