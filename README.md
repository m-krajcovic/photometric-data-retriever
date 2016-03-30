# Nástroj pro vyhledání fotometrických dat stelárních objektů

Cílem práce je pro Ústav Teoretické Fyziky a Astrofyziky PřF MU vytvořit nástroj usnadňující získávání fotometrických dat (tj. údajů o naměřené světelnosti) proměnných hvězd z on-line databází.

V současné době astronomové ručně vyhledají hvězdu v databázi VSX (https://www.aavso.org/vsx/), ze které vedou odkazy do zhruba dvaceti jiných databází poskytujících data ve formě tabulek obsahujících čas, naměřenou světelnost a další údaje. Každá z těchto databází ale má trochu jiný formát tabulky, takže data je třeba vždy upravit postupem specifickým pro každou databázi.

Požadovaný nástroj zadaný objekt vyhledá ve všech dostupných databázích, a provede převedení dat na jednotný textový formát se sloupci oddělenými mezerami, ve kterém v prvním sloupci bude čas (HJD - Heliocentric Julian Day), ve druhém sloupci hvězdná magnituda, a případně v dalších sloupcích doplňující údaje. Volitelně pak sjednocená data nahraje do systému pro vedení pozorovacích deníků MECA.

Přidání dalších on-line databází musí být jednoduché, proto součástí zadání je vytvoření systému pluginů implementujících přístup do jednotlivých databází a programovatelnou úpravu dat. Plugin pro další databázi by mělo být možné implementovat v libovolném programovacím jazyku, zejména v Pythonu, který astronomové umí a snadno se v něm zpracovávají textová data.

Při vývoji použijte iterativní přístup, při kterém budete často konzultovat postup práce se zadavatelem. Důležité kritérium pro hodnocení bude spokojenost zadavatele s výslednou aplikací.

