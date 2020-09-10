# java -jar SLang-1.0-SNAPSHOT-all.jar -m ASSEMBLE -in iinc.s -out Iinc.class

c Iinc
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
        i GETSTATIC java/lang/System out Ljava/io/PrintStream;
        i LDC s 2834+9823=
        i INVOKEVIRTUAL java/io/PrintStream print (Ljava/lang/String;)V

        i BIPUSH 2834
        i ISTORE 0
        i IINC 0 9823
        i GETSTATIC java/lang/System out Ljava/io/PrintStream;
        i ILOAD 0
        i INVOKEVIRTUAL java/io/PrintStream println (I)V

		i RETURN
	em
ec
