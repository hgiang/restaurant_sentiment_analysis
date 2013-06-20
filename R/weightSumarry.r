freq <- c(0.713,0.633,0.680,0.607)
pres <- c(0.719,0.623,0.675,0.655)
tfidf <- c(0.642,0.623,0.657,0.645)
df <- rbind(freq,pres,tfidf)
df <- data.frame(df)
barplot(as.matrix(df), xlab="Methods", ylab= "F-Measure",
   beside=TRUE, ylim=c(0,1), col=rainbow(3))
legend("topleft", c("Frequenct Count","Presence","TF-IDF"), cex=0.9, 
   bty="n", fill=rainbow(3))