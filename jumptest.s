# java -jar SLang-1.0-SNAPSHOT-all.jar -m ASSEMBLE -in compile.s -out CmpTest.class

c CmpTest
	a public
	e java/lang/Object

	m <init> ()V
		a public
	em

	m main ([Ljava/lang/String;)V
		a public
		a static

		i INVOKESTATIC Input readInt ()I
		i ISTORE 0

		i INVOKESTATIC Input readInt ()I
		i ISTORE 1

		i ILOAD 0
		i ILOAD 1

		i IF_ICMPEQ 1

		# NE
		i GETSTATIC java/lang/System out Ljava/io/PrintStream;
		i LDC s NE
		i INVOKEVIRTUAL java/io/PrintStream println (Ljava/lang/String;)V
		i RETURN

		# EQ
		i LABEL 1
		i GETSTATIC java/lang/System out Ljava/io/PrintStream;
		i LDC s EQ
		i INVOKEVIRTUAL java/io/PrintStream println (Ljava/lang/String;)V

		i RETURN
	em
ec