program whileLoop;
var a: array[1..5]of integer; i1 :integer;

begin
   a := 10;
   while  a < 20  do
   
   begin
      write(a);
      a[0] := a + 1;
   end;
end.