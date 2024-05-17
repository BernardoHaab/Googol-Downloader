# Executar projeto

- Requisito Java 17

Para executar o código já compilado o comando usado é:

```
java -jar .\Dowloader.jar .\lib\properties
```

Caso seja necessário recompilar o código os comando utilizados são:

```
javac -encoding UTF-8 .\src\googol\*.java -d .\ -cp ".\\lib\\jsoup-1.17.2.jar"
jar cfe Downloader.jar googol.App .\googol\*.class
```