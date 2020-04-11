//================================================================================================
//Drum: Запуск маслосистемы
Drum.PMS.cond[1]:= DGI.PNS_ON and ANB.PmsmN_U1v;
Drum.PMS.cond[2]:= Drum.PMS.timer[2]>= Drum.PMS.limTimer[2]; //5 sek
Drum.PMS.cond[3]:= Drum.PMS.timer[3]>= Drum.PMS.limTimer[3] or DGI.PNU_ON and ANB.dPm_g_U1v;
//------------------------------------------------------------------------------------------------
DRM_Processing(Drum.PMS, 3);
//------------------------------------------------------------------------------------------------
drm.PNS_ON_PMS 	:= Drum.PMS.state = 1;
TS.StabMsmN_PMS := Drum.PMS.state = 2;
drm.PNU_ON_PMS 	:= Drum.PMS.state = 3;
TS.PMS := Drum.PMS.state >= 1 and Drum.PMS.state <= 3;
//------------------------------------------------------------------------------------------------
End_drum := true;
//------------------------------------------------------------------------------------------------

//================================================================================================
//Drum: Заполнение нагнетателя
Drum.ZN.cond[1]:= DGI.Kr5_ON.PV and DGI.Kr4_ON.PV;
Drum.ZN.cond[2]:= Drum.ZN.timer[2]>= Drum.ZN.limTimer[2]; //10 sek
Drum.ZN.cond[3]:= DGI.Kr5_OF.PV and DGI.Kr4_ON.PV;
Drum.ZN.cond[4]:= Drum.ZN.timer[4]>= Drum.ZN.limTimer[4]; //60 sek
Drum.ZN.cond[5]:= AnParSP.PinN_Uv1 and CalcParSP.dPinNpo_dT_Un1; 
Drum.ZN.cond[6]:= DGI.Kr6_ON.PV and DGI.Kr1_ON.PV and DGI.Kr6r_OF.PV;
Drum.ZN.cond[7]:= DGI.Kr4_OF.PV;
//------------------------------------------------------------------------------------------------
DRM_Processing(Drum.ZN, 7);
//------------------------------------------------------------------------------------------------
drm.Kr4_ON_ZN 	:= Drum.ZN.state >= 1 and Drum.ZN.state <= 5;
drm.Kr5_ON_ZN 	:= Drum.ZN.state = 1 or Drum.ZN.state = 2;
drm.Kr5_OF_ZN 	:= Drum.ZN.state = 3;
drm.Kr1_ON_ZN 	:= Drum.ZN.state = 6;
drm.Kr6_ON_ZN 	:= Drum.ZN.state = 6;
drm.Kr6r_OF_ZN 	:= Drum.ZN.state = 6;
drm.Kr4_OF_ZN 	:= Drum.ZN.state = 7;
//------------------------------------------------------------------------------------------------
End_drum := true;
//------------------------------------------------------------------------------------------------
