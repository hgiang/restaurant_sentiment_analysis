/install.packages('ElemStatLearn')
library('ElemStatLearn')
library("klaR")
library("caret")
train <- read.csv("train.csv", header=TRUE)
x = train [,2:ncol(train)]
y = train$label
model = train(x,y,'nb',trControl=trainControl(method='cv',number=10))