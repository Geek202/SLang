# java -jar SLang-1.0-SNAPSHOT-all.jar -m ASSEMBLE -in hello.s -out Hello.class

c Hello
	a public
	e java/lang/Object
	
	m <init> ()V
		a public
	em
	
	m main ([Ljava/lang/String;)V
		a public
		a static
		
		i GETSTATIC java/lang/System out Ljava/io/PrintStream;
		i LDC s Hello, World!
		i INVOKEVIRTUAL java/io/PrintStream println (Ljava/lang/String;)V
		i RETURN
	em
ec
