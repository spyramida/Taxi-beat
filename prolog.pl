
isBetween(A,T,B) :- T >= A, T =< B.
%isBetweem(2200,T,0) :- T >= 2200, T <2400.

belongsTo(X,L) :- node(_,_,X,L),line(L,_,_,_).

next(X,Y) :- node(_,_,X,L), node(_,_,Y,L), X < Y, line(L,_,O,_),not(O == -1).
next(X,Y) :- node(_,_,X,L), node(_,_,Y,L), X > Y, line(L,_,O,_),not(O == yes).

highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,motorway,_,_),next(X,Y),W is 6.4.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,motorway_link,_,_),next(X,Y),W is 6.4.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,trunk,_,_),next(X,Y),W is 5.4.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,trunk_link,_,_),next(X,Y),W is 5.4.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,primary,_,_),next(X,Y),W is 4.3.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,primary_link,_,_),next(X,Y),W is 4.3.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,secondary,_,_),next(X,Y),W is 3.5.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,secondary_link,_,_),next(X,Y),W is 3.5.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,tertiary,_,_),next(X,Y),W is 1.5.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,tertiary_link,_,_),next(X,Y),W is 1.5.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,construction,_,_),next(X,Y),W is 0.35.	
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,residential,_,_),next(X,Y),W is 0.45.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,footway,_,_),next(X,Y),W is 0.000001.	
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,pedestrian,_,_),next(X,Y),W is 0.000001.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,steps,_,_),next(X,Y),W is 0.000001.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,bridleway,_,_),next(X,Y),W is 0.000001.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,cycleway,_,_),next(X,Y),W is 0.000001.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,path,_,_),next(X,Y),W is 0.000001.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,track,_,_),next(X,Y),W is 0.01.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,living_street,_,_),next(X,Y),W is 0.1.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,A,_,_),next(X,Y),W is 0.2.
highwayVel(X,Y,W) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),line(Z,_,_,_),not(next(X,Y)),W is 0.00000001.


trafDef(light,1.4).
trafDef(medium,2.4).
trafDef(high,6).

trafficVel(X,Y,T,V) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),traffic(Z,A,B,K),isBetween(A,T,B),trafDef(K,V).
trafficVel(X,Y,T,V) :- node(_,_,X,Z),node(_,_,Y,Z),not(X==Y),not((traffic(Z,A,B,_),isBetween(A,T,B))),T >=0,T < 2399,V is 1.

velocity(X,Y,T,V) :- trafficVel(X,Y,T,A),highwayVel(X,Y,B),V is A/B.