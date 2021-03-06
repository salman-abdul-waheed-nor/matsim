package playground.dhosse.scenarios.generic.population.io.mid;

public class MiDConstants {
	
	private MiDConstants(){};

	public static final String SEX_MALE = "1";
	public static final String SEX_FEMALE = "2";
	public static final String NAN = "NaN";
	
	//households
	public static final String HOUSEHOLD_ID = "hhid";
	public static final String SOURCE_ID = "sourceid";
	public static final String HOUSEHOLD_WEIGHT = "hh_gew";
	public static final String HOUSEHOLD_SIZE_CLASS = "h01";
	public static final String HOUSEHOLD_SIZE = "h02";
	public static final String HOUSEHOLD_N_PERSONS_U6 = "hhgr06";
	public static final String HOUSEHOLD_N_PERSONS_U14 = "hhgr14";
	public static final String HOUSEHOLD_N_PERSONS_U18 = "hhgr18";
	public static final String HOUSEHOLD_N_ADULTS = "anzerw";
	public static final String HOUSEHOLD_TYPE = "hhtyp";

	public static final String BUNDESLAND = "bland";
	public static final String WEST_EAST = "westost";
	public static final String REGION_TYPE = "rtyp";
	public static final String REGION_TYPE_DIFF = "rtypd7";
	public static final String DISTRICT_TYPE = "ktyp";
	public static final String DISTRICT_TYPE_AGGREGATED = "kyp_zsg";
	public static final String MUN_TYPE_4 = "sgtyp";
	public static final String MUN_TYPE_D = "sgtypd";
	
	//persons
	public static final String PERSON_ID = "pid";
	public static final String PERSON_WEIGHT = "p_gew";
	public static final String PERSON_INTERVIEW_TYPE = "int_typ9";
	public static final String PERSON_CAR_AVAIL = "p01_1";
	public static final String PERSON_BIKE_AVAIL = "p02";
	public static final String PERSON_BIKE_HELMET = "p021";
	public static final String PERSON_BIKE_USAGE = "p033";
	public static final String PERSON_CAR_USAGE = "p031";
	public static final String PERSON_PT_USAGE = "p032";
	public static final String PERSON_LONG_DISTANCE_TRAIN_USAGE = "p034";
	public static final String PERSON_PLANE_USAGE = "p035";
	public static final String PERSON_DISTANCE_TO_BUS = "p052";
	public static final String PERSON_DISTANCE_TO_TRAIN = "p054";
	public static final String PERSON_SEX = "hp_sex";
	public static final String PERSON_AGE = "hp_alter";
	public static final String PERSON_AGE_GROUP_1 = "hp_altg1";
	public static final String PERSON_AGE_GROUP_2 = "hp_altg2";
	public static final String PERSON_AGE_GROUP_3 = "hp_altg3";
	public static final String PERSON_EMPLOYED = "hp_beruf";
	public static final String PERSON_ACQUISITION_CATEGORY = "hp_bkat";
	public static final String PERSON_MAIN_OCCUPATION = "hp_taet";
	public static final String PERSON_OCCUPATION = "hp_besch";
	public static final String PERSON_GRADUATION = "p14a";
	public static final String PERSON_SCHOOL_TYPE = "p14b";
	public static final String PERSON_COLLEGE_GRADE = "p14_1";
	public static final String PERSON_WORKING_TIME = "p16_1";
	public static final String PERSON_WALK_ACCESS_SCHOOL = "p0411_3";
	public static final String PERSON_WALK_ACCESS_SHOP = "p0411_4";
	public static final String PERSON_BIKE_ACCESS_WORK = "p0412_1";
	public static final String PERSON_BIKE_ACCESS_EDUCATION = "p0412_2";
	public static final String PERSON_BIKE_ACCESS_SCHOOL = "p0412_3";
	public static final String PERSON_BIKE_ACCESS_SHOP = "p0412_4";
	public static final String PERSON_PT_ACCESS_WORK = "p0413_1";
	public static final String PERSON_PT_ACCESS_EDUCATION = "p0413_2";
	public static final String PERSON_PT_ACCESS_SCHOOL = "p0413_3";
	public static final String PERSON_PT_ACCESS_SHOP = "p0413_4";
	public static final String PERSON_PT_ACESS = "p0413";
	public static final String PERSON_CAR_ACCESS_WORK = "p0414_1";
	public static final String PERSON_CAR_ACCESS_EDUCATION = "p0414_2";
	public static final String PERSON_CAR_ACCESS_SCHOOL = "p0414_3";
	public static final String PERSON_CAR_ACCESS_SHOP = "p0414_4";
	public static final String PERSON_TICKET_TYPE = "p070";
	public static final String PERSON_TICKET_TYPE_DIFF = "p070_a";
	public static final String PERSON_NONLOCAL_TRIPS = "p10";
	public static final String PERSON_LICENSE = "p06";
	public static final String PERSON_LICENSE_SCOOTER = "p061_1";
	public static final String PERSON_LICENSE_MOTORCYCLE = "p061_2";
	public static final String PERSON_LICENSE_CAR = "p061_3";
	public static final String PERSON_LICENSE_PKW = "hp_pkwfs";
	public static final String PERSON_LICENSE_HGV = "p061_4";
	public static final String PERSON_LICENSE_REJECTED = "p061_5";
	public static final String PERSON_LICENSE_UNKNOWN = "p061_6";
	public static final String PERSON_LICENSE_SCOOTER_SINCE = "p061_1j";
	public static final String PERSON_LICENSE_MOTORCYCLE_SINCE = "p061_2j";
	public static final String PERSON_LICENSE_CAR_SINCE = "p061_3j";
	public static final String PERSON_LICENSE_HGV_SINCE = "p061_4j";
	public static final String PERSON_HANDY = "p19_1_1";
	public static final String PERSON_COMPUTER = "p19_1_2";
	public static final String PERSON_NAVIGATION_DEVICE = "p19_1_3";
	public static final String PERSON_NO_DEVICE = "p19_1_4";
	public static final String PERSON_DEVICE_REJECTED = "p19_1_5";
	public static final String PERSON_DEVICE_UNKNOWN = "p19_1_6";
	public static final String PERSON_WALKING_DIFFICULTIES = "p091_1";
	public static final String PERSON_VISUALLY_IMPAIRED = "p091_2";
	public static final String PERSON_OTHER_HANDICAP = "p091_3";
	public static final String PERSON_NO_HANDICAP = "p091_4";
	public static final String PERSON_HANDICAP_REJECTED = "p091_5";
	public static final String PERSON_HANDICAP_UNKNOWN = "p091_6";
	public static final String PERSON_HANDICAP = "gesein";
	public static final String PERSON_LIMITED_BY_HANDICAP = "p092";
	public static final String PERSON_RESIDENCE_SINCE = "p08";
	public static final String WEAHER_CONDITION = "s03";
	public static final String PERSON_CAR_AVAIL_AT_DD = "s04";
	public static final String REGULAR_DD = "s01";
	public static final String NOT_REGULAR_VACATION_D = "s02_1";
	public static final String NOT_REGULAR_VACATION_NOT_D = "s02_2";
	public static final String NOT_REGULAR_SICK = "s02_3";
	public static final String NOT_REGULAR_NOT_MOBILE = "s02_4";
	public static final String NOT_REGULAR_MISC = "s02_5";
	public static final String NOT_REGULAR_REJECTED = "s02_6";
	public static final String NOT_REGULAR_UNKNOWN = "s02_7";
	public static final String PERSON_MOBILE = "mobil";
	public static final String PERSON_OCCUPATION_MOBILE = "rbw0";
	public static final String PERSON_OCCUPATION_BRANCHE = "rbw1";
	public static final String PERSON_BUSINESS_LEGS = "rbw02";
	public static final String PERSON_BUSINESS_DISTANCE = "rbw03";
	public static final String PERSON_BUSINESS_N_WAYS = "rbw04";
	public static final String PERSON_BUSINESS_MODE = "rbw05";
	public static final String PERSON_OTHER_WAYS = "w12";
	public static final String PERSON_N_WAYS = "wege1";
	public static final String PERSON_N_PRIVATE_WAYS = "wege2";
	public static final String PERSON_TOTAL_WAYS = "wege3";
	public static final String PERSON_DISTANCE_KM = "anzkm";
	public static final String PERSON_TTIME_MIN = "anzmin";
	public static final String PERSON_GROUP_9 = "pergrup";
	public static final String PERSON_GROUP_12 = "pergrup1";
	public static final String PERSON_LIFE_PHASE = "lebensph";
	public static final String PERSON_PT_SEGMENT = "ov_seg";
	public static final String PERSON_CO2 = "co2tag_p";
	public static final String YEAR = "stich_j";
	public static final String MONTH = "stich_m";
	public static final String DAY_OF_WEEK = "stichtag";
	public static final String WEEK = "stichwo";
	public static final String SEASON = "saison";
	public static final String HOUSEHOLD_INCOME = "hheink";
	public static final String HOUSEHOLD_ECON_STATE = "oek_stat";
	
	//ways
	public static final String WAY_ID = "wid";
	public static final String WAY_ID_SORTED = "wsid";
	public static final String WAY_WEIGHT = "w_gew";
	public static final String WSOURCE = "wsource";
	public static final String ST_TIME = "st_time";
	public static final String ST_STD = "st_std";
	public static final String ST_MIN = "st_min";
	public static final String ST_STDG = "st_stdg";
	public static final String EN_TIME = "en_time";
	public static final String EN_STD = "en_std";
	public static final String EN_MIN = "en_min";
	public static final String ST_DAT = "st_dat";
	public static final String EN_DAT = "en_dat";
	public static final String PURPOSE = "w04";
	public static final String PURPOSE_DIFF = "w04_dzw";
	public static final String PURPOSE_MISC = "w04_sons";
	public static final String MAIN_PURPOSE = "hwzweck";
	public static final String START_POINT = "w01";
	public static final String END_POINT = "w13";
	public static final String DESTINATION = "w044";
	public static final String WALK = "w05_1";
	public static final String BIKE = "w05_2";
	public static final String SCOOTER = "w05_3";
	public static final String MOTORCYCLE = "w05_4";
	public static final String MOTORCYCLE_DRIVER = "mrad_f";
	public static final String MOTORCYCLE_RIDE = "mrad_mf";
	public static final String CAR = "w05_5";
	public static final String CAR_DRIVER = "pkw_f";
	public static final String CAR_RIDE = "pkw_mf";
	public static final String HGV = "w05_7";
	public static final String HGV_DRIVER = "lkw_f";
	public static final String HGV_RIDE = "lkw_mf";
	public static final String BUS = "w05_11";
	public static final String SUBWAY_TRAM = "w05_12";
	public static final String LIGHT_RAIL = "w05_13";
	public static final String TAXI = "w05_14";
	public static final String FERRY = "w05_15";
	public static final String TRAIN = "w05_16";
	public static final String T_BUS = "w05_17";
	public static final String PLANE = "w05_18";
	public static final String OTHER = "w05_20";
	public static final String MODE_REJECTED = "w05_22";
	public static final String MODE_UNKNOWN = "w05_23";
	public static final String MAIN_MODE_DIFF = "hvm_diff";
	public static final String MAIN_MODE = "hvm";
	public static final String MAIN_MODE_PT = "hvm_oev";
	public static final String MODE_COMBINATION = "vm_kombi";
	
	//travels
	
}
