program Test1;
   var
      Area, Comprimento, Raio : real;
&  begin   #essa linha deve gerar um erro devido ao caracter não conhecido
      Raio := 4;
      Area := 3.14 * Raio * Raio;
      Comprimento := 2 * 3.14 * Raio
end