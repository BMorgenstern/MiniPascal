program whileLoop;
var a: array[-1..5]of integer; i1 :integer;
procedure example;


{* 
	{2-3};
	{2--3};
*}
var x, y, z: integer ;


begin

x:=10;
//y:=-5.0; // error
y:=5; // ok
z := x+y;
write(z) 
end
begin
   a := 10;
   while  a < 20  do
   
   begin
      write(a);
      //i1[0] := -4 // error!!
      a[0] := a + 1;
   end;
end.