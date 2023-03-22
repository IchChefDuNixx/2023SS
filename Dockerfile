# dockerfile for the data science class at TH-Rosenheim
# for the summer term 2023
#
# (c) Prof. Dr. Markus Breunig
#
FROM --platform=$BUILDPLATFORM jupyter/datascience-notebook:2023-01-09

RUN pip install xgboost==1.7.3
RUN pip install lightgbm==3.3.4
RUN pip install category_encoders==2.6.0


