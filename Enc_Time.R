key_Length<- c(64,128,256,512,1024,2048,3072)
Encryption_Time<-c(130434,380612,592302,2879825,6046179,25815367,104387907)
plot(key_Length, Encryption_Time) 
lines(key_Length, Encryption_Time,lwd=2) 
