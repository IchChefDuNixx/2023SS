from cx_Freeze import setup, Executable

# Dependencies are automatically detected, but it might need
# fine tuning.
build_options = {'packages': [], 'excludes': []}

base = 'console'

executables = [
    Executable('flask_extractor.py', base=base),
    Executable('flask_guy.py')
]

setup(name='Flask Extractor',
      version = '1.3',
      description = '',
      options = {'build_exe': build_options},
      executables = executables)
