FILES=./PPhase1-Test/PPhase1-Test/*
for f in $FILES
do
echo "running on $f file..."
java -jar OurSQL.jar < $f/in.txt > $f/myout.txt 2> $f/err.txt
diff -sqBb $f/out.txt $f/myout.txt 
done
