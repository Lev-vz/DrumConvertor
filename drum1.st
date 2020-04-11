if Drum.PMS.enable then
	//--------------------- Обнуление таймеров при старте графа --------------------------
	if Drum.PMS.state = 0 then				
		for i = 0 to 9 do					
			Drum.PMS.timer[i] = 0.0;		
		end_for;
		Drum.PMS.state := 1;				//автоматический переход на начало выполнения
	end_if;
	//------------------------------------------------------
	elsif Drum.PMS.state = 1 then			
		if DGI.PNS_ON and ANB.PmsmN_U1v 				then Drum.PMS.state := 2; 	end_if;
	//------------------------------------------------------
	elsif Drum.PMS.state = 2 then
		Drum.PMS.timer[1] := Drum.PMS.timer[1] + cycle;									
		if 	Drum.PMS.timer[1]>= Drum.PMS.limTimer[1]	then Drum.PMS.state := 3;	end_if; 
	//------------------------------------------------------
	elsif Drum.PMS.state = 3 then			
		if DGI.PNU_ON and ANB.dPm_g_U1v 				then Drum.PMS.state := 4; 	end_if;
	//------------------------------------------------------
	elsif Drum.PMS.state = 4 then
		Drum.PMS.complete := true;
	end_if;
	//--- выходы барабана ---
	subMode.PMS := Drum.PMS.state = 1 or Drum.PMS.state = 2 or Drum.PMS.state = 3 or Drum.PMS.state = 4;
	drm.PNS_ON 	:= Drum.PMS.state = 1;
	TS.StabMsmN := Drum.PMS.state = 2;
	drm.PNU_ON 	:= Drum.PMS.state = 3;
else
	if Drum.APK.state <> 0 then
		Drum.PMS.state := 0;
		//--- обнуление выходов при досрочном выходе из барабана
		drm.PNS_ON 	:= false;
		TS.StabMsmN := false;
		drm.PNU_ON 	:= false;
		subMode.PMS := false;
	end_if;
end_if;
