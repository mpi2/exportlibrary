--
-- Copyright (C) 2013 Julian Atienza Herrero <j.atienza at har.mrc.ac.uk>
--
-- MEDICAL RESEARCH COUNCIL UK MRC
--
-- Harwell Mammalian Genetics Unit
--
-- http://www.har.mrc.ac.uk
--
-- Licensed under the Apache License, Version 2.0 (the "License"); you may not
-- use this file except in compliance with the License. You may obtain a copy of
-- the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
-- WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
-- License for the specific language governing permissions and limitations under
-- the License.
--

CREATE TABLE centre
    (
        centre_id INT  NOT NULL ,
        full_name VARCHAR(255),
        short_name VARCHAR(20) NOT NULL,
        address VARCHAR(1024),
        telephone_number VARCHAR(20),
        contact_name VARCHAR(255),
        url VARCHAR(255),
        imits_name VARCHAR(55),
        PRIMARY KEY (centre_id)
    );
    
    
 
INSERT INTO centre (centre_id, full_name, short_name, address, telephone_number, contact_name, url, imits_name) VALUES (1, 'Baylor College of Medicine', 'Bay', '', null, null, null, 'BCM');
INSERT INTO centre (centre_id, full_name, short_name, address, telephone_number, contact_name, url, imits_name) VALUES (2, 'Centre for Modeling Human Disease', 'Cmhd', '', null, null, null, '');
INSERT INTO centre (centre_id, full_name, short_name, address, telephone_number, contact_name, url, imits_name) VALUES (3, 'German Mouse clinic', 'Gmc', '', null, null, null, '');
INSERT INTO centre (centre_id, full_name, short_name, address, telephone_number, contact_name, url, imits_name) VALUES (4, 'MRC Harwell', 'H', 'Harwell Science and Innovation Campus, OX11 0RD', null, null, null, 'HARWELL');
INSERT INTO centre (centre_id, full_name, short_name, address, telephone_number, contact_name, url, imits_name) VALUES (5, 'Helmholtz Zentrum Munchen', 'Hmgu', '', null, null, null, 'HMGU');
INSERT INTO centre (centre_id, full_name, short_name, address, telephone_number, contact_name, url, imits_name) VALUES (6, 'Institut Clinique de la Souris', 'Ics', '', null, null, null, 'ICS');
INSERT INTO centre (centre_id, full_name, short_name, address, telephone_number, contact_name, url, imits_name) VALUES (7, 'The Jackson Laboratory', 'J', '', null, null, null, 'JAX');
INSERT INTO centre (centre_id, full_name, short_name, address, telephone_number, contact_name, url, imits_name) VALUES (8, 'The Toronto Centre for Phenogenomics', 'Tcp', '', null, null, null, 'TCP');
INSERT INTO centre (centre_id, full_name, short_name, address, telephone_number, contact_name, url, imits_name) VALUES (9, 'Nanjing University', 'Ning', '', null, null, null, '');
INSERT INTO centre (centre_id, full_name, short_name, address, telephone_number, contact_name, url, imits_name) VALUES (10, 'RIKEN Tsukuba Institute, BioResource Center', 'Rbrc', '', null, null, null, '');
INSERT INTO centre (centre_id, full_name, short_name, address, telephone_number, contact_name, url, imits_name) VALUES (11, 'University of California, Davis', 'Ucd', '', null, null, null, 'UCD');
INSERT INTO centre (centre_id, full_name, short_name, address, telephone_number, contact_name, url, imits_name) VALUES (12, 'Welcome Trust Sanger Institute', 'Wtsi', 'Wellcome Trust Genome Campus, Hinxton, Cambridge CB10 1SA, UK', null, null, null, 'WTSI');



CREATE TABLE strain
    (
        strain_id INT  NOT NULL ,
        strain VARCHAR(255),
        mgi_strain_id VARCHAR(255),
        PRIMARY KEY (strain_id)
    )
    ;
    
    
    INSERT INTO strain (strain_id, strain, mgi_strain_id) VALUES (15, 'C57BL/6J-Tyr<c-Brd>;C57BL/6N', 'C57BL/6JTyr;C57BL/6N');
INSERT INTO strain (strain_id, strain, mgi_strain_id) VALUES (18, 'C57BL/6N', 'MGI:2159965');
INSERT INTO strain (strain_id, strain, mgi_strain_id) VALUES (19, 'C57BL/6NCrl', 'MGI:2683688');
INSERT INTO strain (strain_id, strain, mgi_strain_id) VALUES (35, 'C57BL/6NTac', 'MGI:2164831');
INSERT INTO strain (strain_id, strain, mgi_strain_id) VALUES (44, 'C57BL/6Dnk', 'MGI:4830588');
INSERT INTO strain (strain_id, strain, mgi_strain_id) VALUES (61, 'C57BL/6NJ', 'MGI:3056279');




CREATE TABLE genotype_new
    (
        genotype_id INT NOT NULL ,
        genotype VARCHAR(255),
        source VARCHAR(255),
        allele_name VARCHAR(255),
        allele_type VARCHAR(255),
        gene_id VARCHAR(255),
        gene_name VARCHAR(255),
        gene_symbol VARCHAR(255),
        definative_name VARCHAR(255),
        emma_id VARCHAR(15),
        komp_id VARCHAR(15),
        jax_id VARCHAR(15),
        international_strain_name VARCHAR(512),
        ensembl_id VARCHAR(255),
        show_this BOOLEAN,
        allele_id VARCHAR(15),
        epd_id VARCHAR(45),
        stocklist_id VARCHAR(15),
        HTGT_project_id VARCHAR(15),
        centre_id INT,
        strain_id INT,
        MGI_strain_id VARCHAR(45),
        PRIMARY KEY (genotype_id)
    );


INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (93, 'BL1487', 'Ube2e2<sup>tm1b(KOMP)Wtsi</sup>', 11, 35, 'MGI:2384997', 'Ube2e2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (116, 'BL1512', 'Htra4<sup>tm1b(KOMP)Wtsi</sup>', 11, 35, 'MGI:3036260', 'Htra4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1154, 'BL1601', 'D17Wsu92e<sup>Gt(IST12471H5)Tigm</sup>', 11, null, 'MGI:106281', 'D17Wsu92e');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (86, 'BL1690', 'Fam210b<sup>tm1b(KOMP)Mbp</sup>', 11, 35, 'MGI:1914267', 'Fam210b');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (66, 'BL1997', 'Mtnr1b<sup>tm1.1(KOMP)Vlcg</sup>', 11, 19, 'MGI:2181726', 'Mtnr1b');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (26, 'BL2067', 'Slc9b2<sup>tm1.1(KOMP)Vlcg</sup>', 11, 19, 'MGI:2140077', 'Slc9b2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (126, 'BL2226', 'Abca8b<sup>tm1b(EUCOMM)Wtsi</sup>', 11, 35, 'MGI:1351668', 'Abca8b');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (125, 'BL2227', 'Fgfbp3<sup>tm1b(KOMP)Wtsi</sup>', 11, 35, 'MGI:1919764', 'Fgfbp3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (84, 'BL2273', 'Tm4sf4<sup>tm1.1(KOMP)Vlcg</sup>', 11, 19, 'MGI:2385173', 'Tm4sf4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (31, 'BL2275', 'Ttc30a2<sup>tm1.1(KOMP)Vlcg</sup>', 11, 19, 'MGI:3700200', 'Ttc30a2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (8, 'BL2277', 'Hpcal4<sup>tm1.1(KOMP)Vlcg</sup>', 11, 35, 'MGI:2157521', 'Hpcal4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (29, 'BL2301', 'Nudt14<sup>tm1.1(KOMP)Vlcg</sup>', 11, 35, 'MGI:1913424', 'Nudt14');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (96, 'BL2321', 'Rabl6<sup>tm1b(KOMP)Wtsi</sup>', 11, 35, 'MGI:2442633', 'Rabl6');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (20, 'BL2327', 'Scarf2<sup>tm1.1(KOMP)Vlcg</sup>', 11, 35, 'MGI:1858430', 'Scarf2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (90, 'BL2351', 'Uts2b<sup>tm1b(KOMP)Mbp</sup>', 11, 35, 'MGI:2677064', 'Uts2b');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (110, 'BL2359', 'Lypd6b<sup>tm1b(KOMP)Wtsi</sup>', 11, 35, 'MGI:1919147', 'Lypd6b');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (137, 'BL2398', 'A330021E22Rik<sup>tm1b(KOMP)Wtsi</sup>', 11, 19, 'MGI:2443778', 'A330021E22Rik');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (97, 'BL2428', 'Olah<sup>tm1b(KOMP)Wtsi</sup>', 11, 19, 'MGI:2139018', 'Olah');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (136, 'BL2461', 'Asb15<sup>tm1b(KOMP)Wtsi</sup>', 11, 19, 'MGI:1926160', 'Asb15');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (594, 'E105JM8N4-70', 'Ino80<sup>tm1b(EUCOMM)Hmgu</sup>', 6, 35, 'MGI:1915392', 'Ino80');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (884, 'E112JM8N4-2', '4933430I17Rik<sup>tm1b(EUCOMM)Wtsi</sup>', 6, 35, 'MGI:3045314', '4933430I17Rik');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1157, 'E184JM8N4-12', 'Laptm4a<sup>tm1b(EUCOMM)Hmgu</sup>', 6, 35, 'MGI:108017', 'Laptm4a');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1182, 'E201JM8N4-54', 'Iah1<sup>tm1b(EUCOMM)Hmgu</sup>', 6, 35, 'MGI:1914982', 'Iah1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1152, 'E202JM8N4-24', 'Baiap2l2<sup>tm1b(EUCOMM)Hmgu</sup>', 6, 35, 'MGI:2652819', 'Baiap2l2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1181, 'E203JM8N4-28', 'Rnf144b<sup>tm1b(EUCOMM)Hmgu</sup>', 6, 35, 'MGI:2384986', 'Rnf144b');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1524, 'IP00004326a', '4933430I17Rik<sup>tm1a(EUCOMM)Wtsi</sup>', 6, 35, 'MGI:3045314', '4933430I17Rik');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1350, 'IP00004675a', 'Plscr1<sup>tm1b(EUCOMM)Hmgu</sup>', 6, 35, 'MGI:893575', 'Plscr1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1115, 'GSF-EPD0227_6_F12-1-2', 'Car4<sup>tm1b(EUCOMM)Wtsi</sup>', 5, 35, 'MGI:1096574', 'Car4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (181, 'GSF-EPD0243_5_D11-1-1', 'Rxfp2<sup>tm1b(EUCOMM)Wtsi</sup>', 5, 35, 'MGI:2153463', 'Rxfp2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (180, 'GSF-EPD0369_6_A06-1-1', 'P2rx7<sup>tm1b(EUCOMM)Wtsi</sup>', 5, 35, 'MGI:1339957', 'P2rx7');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (179, 'GSF-HEPD0550_6_G09-1-1', 'Ndfip2<sup>tm1b(EUCOMM)Hmgu</sup>', 5, 35, 'MGI:1923523', 'Ndfip2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (188, 'HMGU-EPD0445_2_D06-1-1', 'Efna5<sup>tm1b(EUCOMM)Wtsi</sup>', 5, 35, 'MGI:107444', 'Efna5');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (370, 'H-Adh5-G04-TM1B', 'Adh5<sup>tm1b(EUCOMM)Wtsi</sup>', 4, 35, 'MGI:87929', 'Adh5');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (375, 'H-Agl-E12-TM1B-2', 'Agl<sup>tm1b(EUCOMM)Wtsi</sup>', 4, 35, 'MGI:1924809', 'Agl');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (377, 'H-CIB2-D04-TM1B', 'Cib2<sup>tm1b(EUCOMM)Wtsi</sup>', 4, 35, 'MGI:1929293', 'Cib2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (366, 'H-Creld2-F05-TM1B', 'Creld2<sup>tm1b(EUCOMM)Hmgu</sup>', 4, 35, 'MGI:1923987', 'Creld2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (355, 'H-Cttn-D11-TM1B', 'Cttn<sup>tm1b(EUCOMM)Hmgu</sup>', 4, 35, 'MGI:99695', 'Cttn');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (382, 'H-CYB5R2-C08-TM1B', 'Cyb5r2<sup>tm1b(EUCOMM)Hmgu</sup>', 4, 35, 'MGI:2444415', 'Cyb5r2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (387, 'H-EAF1-E07-TM1B', 'Eaf1<sup>tm1b(EUCOMM)Wtsi</sup>', 4, 35, 'MGI:1921677', 'Eaf1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (386, 'H-ELMOD1-E10-TM1B', 'Elmod1<sup>tm1b(EUCOMM)Hmgu</sup>', 4, 35, 'MGI:3583900', 'Elmod1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (374, 'H-Fam151b-C08-TM1B', 'Fam151b<sup>tm1b(EUCOMM)Hmgu</sup>', 4, 35, 'MGI:1921192', 'Fam151b');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (385, 'H-GPR33-A05-TM1B', 'Gpr33<sup>tm1b(EUCOMM)Hmgu</sup>', 4, 35, 'MGI:1277106', 'Gpr33');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (359, 'H-Il17rd-G11-TM1B', 'Il17rd<sup>tm1b(EUCOMM)Hmgu</sup>', 4, 35, 'MGI:2159727', 'Il17rd');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (388, 'H-ITGA2-G06-TM1B', 'Itga2<sup>tm1b(EUCOMM)Hmgu</sup>', 4, 35, 'MGI:96600', 'Itga2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (376, 'H-Kdm8-A04-TM1B', 'Kdm8<sup>tm1b(EUCOMM)Wtsi</sup>', 4, 35, 'MGI:1924285', 'Kdm8');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (384, 'H-MAPKBP1-H02-TM1B', 'Mapkbp1<sup>tm1b(EUCOMM)Hmgu</sup>', 4, 35, 'MGI:1347004', 'Mapkbp1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (373, 'H-Mxra7-G06-TM1B', 'Mxra7<sup>tm1b(EUCOMM)Wtsi</sup>', 4, 35, 'MGI:1914872', 'Mxra7');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (412, 'H-Rhbdl3-C11-TM1B', 'Rhbdl3<sup>tm1b(EUCOMM)Wtsi</sup>', 4, 35, 'MGI:2179276', 'Rhbdl3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (356, 'H-Slc40a1-A07-TM1B', 'Slc40a1<sup>tm1b(EUCOMM)Hmgu</sup>', 4, 35, 'MGI:1315204', 'Slc40a1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (229, 'H-Tpcn2-C10-TM1B', 'Tpcn2<sup>tm1b(EUCOMM)Hmgu</sup>', 4, 35, 'MGI:2385297', 'Tpcn2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (152, 'JR18553', 'Trip13<sup>tm1.1(KOMP)Vlcg</sup>', 7, 61, 'MGI:1916966', 'Trip13');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (214, 'JR18556', 'Cbln3<sup>tm1.1(KOMP)Vlcg</sup>', 7, 18, 'MGI:1889286', 'Cbln3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (150, 'JR18557', 'Slc1a3<sup>tm1.1(KOMP)Mbp</sup>', 7, 61, 'MGI:99917', 'Slc1a3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (151, 'JR18561', 'Ahrr<sup>tm1b(KOMP)Wtsi</sup>', 7, 61, 'MGI:1333776', 'Ahrr');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (213, 'JR18562', 'C1qtnf5<sup>tm1.1(KOMP)Vlcg</sup>', 7, 61, 'MGI:2385958', 'C1qtnf5');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (492, 'JR18563', 'Loxl1<sup>tm1.1(KOMP)Vlcg</sup>', 7, 18, 'MGI:106096', 'Loxl1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (621, 'JR18565', 'Dmap1<sup>tm1.1(KOMP)Vlcg</sup>', 7, 18, 'MGI:1913483', 'Dmap1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (422, 'JR18568', 'Prokr1<sup>tm1.1(KOMP)Vlcg</sup>', 7, 18, 'MGI:1929676', 'Prokr1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (493, 'JR18570', 'Macrod2<sup>tm1.1(KOMP)Vlcg</sup>', 7, 61, 'MGI:1920149', 'Macrod2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (494, 'JR18572', 'Resp18<sup>tm1.1(KOMP)Vlcg</sup>', 7, 61, 'MGI:1098222', 'Resp18');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (579, 'JR18576', 'Prom2<sup>tm1.1(KOMP)Vlcg</sup>', 7, 61, 'MGI:2138997', 'Prom2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (746, 'JR18584', 'Dixdc1<sup>tm1.1(KOMP)Vlcg</sup>', 7, 18, 'MGI:2679721', 'Dixdc1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (745, 'JR18592', 'Adora2b<sup>tm1.1(KOMP)Vlcg</sup>', 7, 18, 'MGI:99403', 'Adora2b');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (604, 'JR18594', 'H1fx<sup>tm1.1(KOMP)Vlcg</sup>', 7, 61, 'MGI:2685307', 'H1fx');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (605, 'JR18607', 'Arrdc1<sup>tm1.1(KOMP)Vlcg</sup>', 7, 18, 'MGI:2446136', 'Arrdc1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (597, 'JR18608', 'Arrb2<sup>tm1.1(KOMP)Vlcg</sup>', 7, 18, 'MGI:99474', 'Arrb2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (600, 'JR18611', 'Stk16<sup>tm1.1(KOMP)Vlcg</sup>', 7, 61, 'MGI:1313271', 'Stk16');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (724, 'JR18640', 'Kcnh3<sup>tm1.1(KOMP)Vlcg</sup>', 7, 61, 'MGI:1341723', 'Kcnh3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (721, 'JR18646', 'Nsf<sup>tm1b(KOMP)Mbp</sup>', 7, 61, 'MGI:104560', 'Nsf');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (791, 'JR18657', 'Arsk<sup>tm1b(KOMP)Wtsi</sup>', 7, 18, 'MGI:1924291', 'Arsk');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (722, 'JR19073', 'Hspa5<sup>tm1.1(KOMP)Vlcg</sup>', 7, 61, 'MGI:95835', 'Hspa5');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (596, 'JR19459', 'Ghrhr<sup>tm1.1(KOMP)Vlcg</sup>', 7, 61, 'MGI:95710', 'Ghrhr');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (965, 'MAOH', 'Pfn1<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:97549', 'Pfn1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (966, 'MBCZ', 'Tmem189<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:2142624', 'Tmem189');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (968, 'MBGS', 'Acsl4<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1354713', 'Acsl4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (969, 'MBWA', 'Aldh16a1<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1916998', 'Aldh16a1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (970, 'MCBN', 'Rtbdn<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:2443686', 'Rtbdn');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1227, 'MCKW', 'Kifap3<sup>tm1a(KOMP)Wtsi</sup>', 12, 18, 'MGI:107566', 'Kifap3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (972, 'MCMM', '1500011B03Rik<sup>tm2(KOMP)Wtsi</sup>', 12, null, 'MGI:1913486', '1500011B03Rik');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (973, 'MCND', 'Aff3<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:106927', 'Aff3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (974, 'MCQP', 'Cnot6<sup>tm1a(KOMP)Wtsi</sup>', 12, 18, 'MGI:2144529', 'Cnot6');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (975, 'MCRQ', 'Ocm<sup>tm1e(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:97401', 'Ocm');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (396, 'MCSQ', 'Pld5<sup>tm1b(KOMP)Wtsi</sup>', 12, 15, 'MGI:2442056', 'Pld5');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (976, 'MCSZ', 'Dynll1<sup>tm1(KOMP)Wtsi</sup>', 12, 18, 'MGI:1861457', 'Dynll1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (977, 'MCTN', 'Cbx5<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:109372', 'Cbx5');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (978, 'MCTQ', 'Cbx6<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:3512628', 'Cbx6');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (979, 'MCUU', 'Prps1<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:97775', 'Prps1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (980, 'MCVS', 'Tmc6<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1098686', 'Tmc6');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (981, 'MCWY', 'Alg13<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1914824', 'Alg13');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (982, 'MCXD', 'Myo15<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1261811', 'Myo15');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (983, 'MCXN', 'Gle1<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1921662', 'Gle1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (984, 'MCYF', 'Kng2<sup>tm2a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:3027157', 'Kng2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (985, 'MCZD', 'Wdr37<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1920393', 'Wdr37');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (986, 'MCZT', 'Abca4<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:109424', 'Abca4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (987, 'MCZU', 'Actn4<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1890773', 'Actn4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1243, 'MCZX', 'Coq4<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1098826', 'Coq4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (988, 'MDCL', 'Sirt3<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1927665', 'Sirt3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (989, 'MDCM', 'Gpc5<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1194894', 'Gpc5');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (990, 'MDDP', 'Rufy2<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1917682', 'Rufy2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (991, 'MDDR', 'Sag<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:98227', 'Sag');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (992, 'MDDY', 'Map3k1<sup>tm1a(KOMP)Wtsi</sup>', 12, 18, 'MGI:1346872', 'Map3k1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (993, 'MDEF', 'Ppp1r42<sup>tm1a(KOMP)Wtsi</sup>', 12, 18, 'MGI:1921138', 'Ppp1r42');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (994, 'MDEQ', 'Snap47<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1915076', 'Snap47');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (995, 'MDFF', 'Tsfm<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1913649', 'Tsfm');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (996, 'MDGG', 'Kdm4d<sup>tm2a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:3606484', 'Kdm4d');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1251, 'MDGH', 'Tnik<sup>tm3a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1916264', 'Tnik');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (997, 'MDGP', 'Oas1g<sup>tm3e(KOMP)Wtsi</sup>', 12, 18, 'MGI:97429', 'Oas1g');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (998, 'MDGW', 'Tm9sf4<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:2139220', 'Tm9sf4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (999, 'MDHD', 'Calcoco2<sup>tm1(KOMP)Wtsi</sup>', 12, null, 'MGI:1343177', 'Calcoco2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1000, 'MDHP', 'Gm13547<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:3650473', 'Gm13547');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1252, 'MDHR', 'Skida1<sup>tm1(KOMP)Wtsi</sup>', 12, null, 'MGI:1919918', 'Skida1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1001, 'MDJD', 'Glt8d2<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1922032', 'Glt8d2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1253, 'MDJF', 'Glg1<sup>tm1a(KOMP)Wtsi</sup>', 12, 18, 'MGI:104967', 'Glg1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1002, 'MDJJ', 'Orc3<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1354944', 'Orc3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1003, 'MDJL', 'Seh1l<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1919374', 'Seh1l');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1004, 'MDJV', 'Wnt3<sup>tm2a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:98955', 'Wnt3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1005, 'MDJZ', 'Vps53<sup>tm1a(KOMP)Wtsi</sup>', 12, 18, 'MGI:1915549', 'Vps53');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1006, 'MDKE', 'Ccdc77<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1914450', 'Ccdc77');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1007, 'MDKG', 'Dph2<sup>tm2(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1914978', 'Dph2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1008, 'MDKN', 'Cand2<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1914338', 'Cand2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1009, 'MDKQ', 'Wfdc18<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:107506', 'Wfdc18');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1010, 'MDKX', 'Tff1<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:88135', 'Tff1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1011, 'MDKZ', 'Slc25a4<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1353495', 'Slc25a4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1012, 'MDLF', 'Lztr1<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1914113', 'Lztr1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1257, 'MDLQ', 'Cmtm4<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:2142888', 'Cmtm4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1013, 'MDLT', 'Fam96a<sup>tm2a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1915500', 'Fam96a');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1014, 'MDLW', 'Rpgrip1l<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1920563', 'Rpgrip1l');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1015, 'MDLX', 'Dlg2<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1344351', 'Dlg2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1259, 'MDML', 'Ccl22<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1306779', 'Ccl22');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1016, 'MDMP', 'Mapk10<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1346863', 'Mapk10');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1017, 'MDNC', 'Zscan10<sup>tm2a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:3040700', 'Zscan10');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1018, 'MDNJ', 'Syt1<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:99667', 'Syt1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1019, 'MDNQ', 'Naga<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1261422', 'Naga');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1020, 'MDNU', 'Il10rb<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:109380', 'Il10rb');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1021, 'MDRA', 'Fbxl7<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:3052506', 'Fbxl7');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1022, 'MDRB', 'Fggy<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1922828', 'Fggy');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1023, 'MDRQ', 'Hacl1<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1929657', 'Hacl1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1024, 'MDRV', 'Farsa<sup>tm2a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1913840', 'Farsa');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1025, 'MDRZ', 'Gbf1<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1861607', 'Gbf1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1026, 'MDSU', 'Mrap2<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:3609239', 'Mrap2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1027, 'MDTN', 'Phtf2<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1916020', 'Phtf2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1028, 'MDTQ', 'Ralb<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1927244', 'Ralb');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1029, 'MDTX', 'Dmxl2<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:2444630', 'Dmxl2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1030, 'MDTZ', 'Fbxo47<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1920223', 'Fbxo47');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1031, 'MDUH', 'Trpc2<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:109527', 'Trpc2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1261, 'MDUJ', 'Setd4<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:2136890', 'Setd4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1032, 'MDUP', 'Rnaseh2c<sup>tm1(KOMP)Wtsi</sup>', 12, 18, 'MGI:1915459', 'Rnaseh2c');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1033, 'MDUQ', 'Pacsin3<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1891410', 'Pacsin3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (207, 'MDUS', 'Dusp3<sup>tm1b(KOMP)Wtsi</sup>', 12, 15, 'MGI:1919599', 'Dusp3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1034, 'MDUU', '4932438H23Rik<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1921637', '4932438H23Rik');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1035, 'MDUV', 'Dynlrb1<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1914318', 'Dynlrb1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1262, 'MDUW', 'Usp22<sup>tm1a(KOMP)Wtsi</sup>', 12, 18, 'MGI:2144157', 'Usp22');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1263, 'MDVB', 'Aspm<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1334448', 'Aspm');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1264, 'MDVQ', 'Fyn<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:95602', 'Fyn');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1036, 'MDVT', 'Jhdm1d<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:2443388', 'Jhdm1d');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1037, 'MDWB', 'Apol7a<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1923011', 'Apol7a');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1265, 'MDWE', 'Dctn1<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:107745', 'Dctn1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1038, 'MDWK', 'Arpc1b<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1343142', 'Arpc1b');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1266, 'MDWN', 'G3bp2<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:2442040', 'G3bp2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1039, 'MDWP', 'Dars2<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:2442510', 'Dars2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1040, 'MDXJ', 'Myrfl<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:2685085', 'Myrfl');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1041, 'MDXK', 'Mlec<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1924015', 'Mlec');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1269, 'MDXN', 'Map2k7<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1346871', 'Map2k7');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1042, 'MDXP', 'Tcf4<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:98506', 'Tcf4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1043, 'MDXZ', 'Cmtm6<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:2447165', 'Cmtm6');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1044, 'MDYB', 'Ezr<sup>tm2a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:98931', 'Ezr');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1045, 'MDYD', 'Cabp1<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1352750', 'Cabp1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1046, 'MDYN', 'Kcnv2<sup>tm1Wtsi</sup>', 12, null, 'MGI:2670981', 'Kcnv2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1273, 'MDYQ', 'Fam175b<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1926116', 'Fam175b');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1047, 'MDYT', 'Gprc5b<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1927596', 'Gprc5b');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1048, 'MDYU', 'Dopey2<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1917278', 'Dopey2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1049, 'MDYY', 'Sel1l<sup>tm1e(KOMP)Wtsi</sup>', 12, null, 'MGI:1329016', 'Sel1l');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1050, 'MDZE', 'Lamc3<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1344394', 'Lamc3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1051, 'MEAC', 'Actr6<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1914269', 'Actr6');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1279, 'MEAH', 'Rala<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1927243', 'Rala');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1280, 'MEAL', 'Spopl<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1924107', 'Spopl');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1282, 'MEBJ', 'Rasal2<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:2443881', 'Rasal2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1283, 'MEBN', 'Irf7<sup>tm1(KOMP)Wtsi</sup>', 12, 18, 'MGI:1859212', 'Irf7');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1284, 'MEBR', 'Smyd5<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:108048', 'Smyd5');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1052, 'MEBV', 'Fbxo7<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1917004', 'Fbxo7');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1053, 'MEBW', 'Oxr1<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:2179326', 'Oxr1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1054, 'MEBX', 'Myo7a<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:104510', 'Myo7a');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1055, 'MEBY', 'Arhgef7<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1860493', 'Arhgef7');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1056, 'MECG', 'Casp12<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1312922', 'Casp12');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1286, 'MECR', 'Ido2<sup>tm3a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:2142489', 'Ido2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1057, 'MECV', 'Apool<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1915367', 'Apool');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1288, 'MECY', '3830417A13Rik<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1917946', '3830417A13Rik');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1289, 'MEDH', 'Pdxk<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1351869', 'Pdxk');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1058, 'MEDS', 'Ccdc160<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:3588225', 'Ccdc160');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1059, 'MEDY', 'Arsg<sup>tm1a(KOMP)Wtsi</sup>', 12, 18, 'MGI:1921258', 'Arsg');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1060, 'MEEC', 'Trim17<sup>tm1e(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1861440', 'Trim17');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (141, 'MEEK', 'Rhox13<sup>tm1a(KOMP)Wtsi</sup>', 12, 18, 'MGI:1920864', 'Rhox13');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1294, 'MEFL', 'Ints2<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1917672', 'Ints2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1295, 'MEFU', 'Elmo1<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:2153044', 'Elmo1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1299, 'MEGM', 'Wbp5<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:109567', 'Wbp5');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1061, 'MEHR', 'Zfp182<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:2442220', 'Zfp182');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (673, 'MEJH', 'Rbmx<sup>tm2a(KOMP)Wtsi</sup>', 12, 18, 'MGI:1343044', 'Rbmx');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (254, 'MEJY', 'Tomm20l<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:1922516', 'Tomm20l');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1062, 'MEKD', 'Tatdn3<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1916222', 'Tatdn3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1063, 'MEKG', 'Dennd1c<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1918035', 'Dennd1c');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (677, 'MEKM', 'Exoc3l2<sup>tm1a(KOMP)Wtsi</sup>', 12, 18, 'MGI:1921713', 'Exoc3l2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1301, 'MELC', 'Ttll11<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1921660', 'Ttll11');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1302, 'MELE', 'Rundc1<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:2144506', 'Rundc1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1304, 'MEMY', 'Sec1<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:1928893', 'Sec1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (264, 'MENA', 'Zfp719<sup>tm1a(EUCOMM)Wtsi</sup>', 12, 18, 'MGI:2444708', 'Zfp719');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1305, 'MENG', 'A830019P07Rik<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:3028035', 'A830019P07Rik');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1064, 'MENP', 'Leprot<sup>tm2a(KOMP)Wtsi</sup>', 12, null, 'MGI:2687005', 'Leprot');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1306, 'MENZ', 'Slc1a4<sup>tm1e(KOMP)Wtsi</sup>', 12, null, 'MGI:2135601', 'Slc1a4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1065, 'MEQD', 'Daam2<sup>tm1a(KOMP)Wtsi</sup>', 12, null, 'MGI:1923691', 'Daam2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1308, 'MEQF', 'Ninj2<sup>tm1a(KOMP)Wtsi</sup>', 12, 18, 'MGI:1352751', 'Ninj2');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1312, 'MEQZ', 'Rasgrp4<sup>tm2a(KOMP)Wtsi</sup>', 12, null, 'MGI:2386851', 'Rasgrp4');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1066, 'MERM', 'Dcdc2c<sup>tm1a(KOMP)Wtsi</sup>', 12, 18, 'MGI:1915761', 'Dcdc2c');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1324, 'MFGY', 'Fanci<sup>tm1a(EUCOMM)Wtsi</sup>', 12, null, 'MGI:2384790', 'Fanci');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (205, 'MLFC', 'Crlf3<sup>tm1b(KOMP)Wtsi</sup>', 12, 15, 'MGI:1860086', 'Crlf3');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (398, 'MNPP', 'Inpp1<sup>tm1b(KOMP)Wtsi</sup>', 12, 15, 'MGI:104848', 'Inpp1');
INSERT INTO genotype_new (genotype_id, genotype, allele_name, centre_id, strain_id, gene_id, gene_symbol) VALUES (1067, 'MSEV', 'Ywhae<sup>tm1e(EUCOMM)Wtsi</sup>', 12, null, 'MGI:894689', 'Ywhae');

