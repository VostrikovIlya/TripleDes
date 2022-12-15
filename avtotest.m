clear;
fu2 = fopen("C:\Users\Ilya\Desktop\YandexDisk\Labs\crypto\lab4\avtotest.txt",'r');
U2 = fscanf(fu2,"%f");
fclose(fu2);
figure(1);
plot(U2);
grid on;
