#!/usr/bin/env python
# coding: utf-8

# %%
# imports
import flask_extractor
import pandasgui


def main():
    df = flask_extractor.main(gui=True)
    pandasgui.show(df)

# %%
if __name__ == "__main__":
    main()