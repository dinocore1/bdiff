
#include <jni.h>

#include <stdint.h>

#include <limits.h>
#include <assert.h>

#ifndef NELEM
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

static inline uint32_t rotl32 (uint32_t n, unsigned int c) {
    const unsigned int mask = (CHAR_BIT*sizeof(n)-1);

    assert ( (c<=mask) &&"rotate by type width or more");
    c &= mask;  // avoid undef behaviour with NDEBUG.  0 overhead for most types / compilers
    return (n<<c) | (n>>( (-c)&mask ));
}

static inline uint32_t rotr32 (uint32_t n, unsigned int c) {
    const unsigned int mask = (CHAR_BIT*sizeof(n)-1);

    assert ( (c<=mask) &&"rotate by type width or more");
    c &= mask;  // avoid undef behaviour with NDEBUG.  0 overhead for most types / compilers
    return (n>>c) | (n<<( (-c)&mask ));
}

static inline uint64_t rotl64 (uint64_t n, unsigned int c) {
    const unsigned int mask = (CHAR_BIT*sizeof(n)-1);

    assert ( (c<=mask) &&"rotate by type width or more");
    c &= mask;  // avoid undef behaviour with NDEBUG.  0 overhead for most types / compilers
    return (n<<c) | (n>>( (-c)&mask ));
}

static inline uint64_t rotr64 (uint64_t n, unsigned int c) {
    const unsigned int mask = (CHAR_BIT*sizeof(n)-1);

    assert ( (c<=mask) &&"rotate by type width or more");
    c &= mask;  // avoid undef behaviour with NDEBUG.  0 overhead for most types / compilers
    return (n>>c) | (n<<( (-c)&mask ));
}

static const char* CLASS_NAME = "com/devsmart/bdiff/buzhash/NativeBuzhash64";

static int64_t HASHMAP[256] = {
        -4964420948893066024,
        7564655870752979346,
        3831662765844904176,
        6137546356583794141,
        -594798593157429144,
        112842269129291794,
        -669528114487223426,
        -1109287713991315740,
        -974081879987450628,
        -1160629452687687109,
        7326573195622447256,
        6410576364588137014,
        5424394867226112926,
        -9103770306483490189,
        2139215297105423308,
        -4232865876030345843,
        -6273872167485304708,
        2891469594365336806,
        6976596177944619528,
        2578166436595196069,
        -5627216606837767319,
        -3592913410653813758,
        92698085241473569,
        -8796603504740353600,
        -4722652817683412901,
        2619856624980352251,
        8886318912347348303,
        -8401480976436315613,
        -7801123389691242517,
        3779987867844568136,
        -6947711303906420817,
        3407244680303549079,
        197092594700490712,
        2970725011242582564,
        3284532136690698432,
        -8478177725643278359,
        -482677293272704124,
        4527320925905780494,
        7277626163180921831,
        4014050679668805482,
        7969120158891947125,
        4300965142756182089,
        -2030825140507191061,
        707006413279611759,
        -7519275600551226667,
        -6360924135797636003,
        2210640064016022649,
        -6410673298797731886,
        -289193436830779917,
        3813634057487595412,
        6911063436971917473,
        8547294963617019503,
        6154022364946197696,
        8175826803456981118,
        -9147084144055124649,
        -18800628192384088,
        -6817826759444601261,
        -1667880028869348243,
        -9082071447080613645,
        9065674809775364834,
        7909671975457870438,
        5683311091615826937,
        -5214481407826501455,
        -693328208225879290,
        3864458965704708566,
        3184808690151788414,
        -8320357513071910606,
        -8200160751728555263,
        -7603456060161050842,
        -3888746125786119271,
        -5552347832537805063,
        3774859742041214532,
        4702249276633814781,
        -4096719924219374161,
        4150930343758163695,
        -311691390498039484,
        -3622597253628401501,
        -3019456038419834778,
        3008729024856518368,
        -6686992125460025861,
        161601140914943624,
        -6803345800057020374,
        3836516331752709628,
        -2207018395996362651,
        -5404080405594186050,
        -5102892484113533015,
        -9048258330105186985,
        -237923595412718844,
        2826893961496978298,
        -5338953178777934760,
        -3246979880425410455,
        2281331448982092637,
        -7065999876450923625,
        8888791547312749291,
        1840067945267344782,
        -7062411921403462023,
        518729297779020562,
        -7536618281581788192,
        1347092278477147782,
        1365943130551420261,
        -5904149397109527665,
        5165118076730241013,
        -7305211479695003402,
        -2773637612724504142,
        6526887576802954450,
        -7403923644694799186,
        5388172503113520870,
        -2279230739761038859,
        -4717761859960318649,
        7807265917042125009,
        6932437597733693250,
        5004478446554740296,
        -4983868948686226820,
        -2089196626022557463,
        806172501569318489,
        8443078202631527623,
        -2537354127574070879,
        -1809183693800895546,
        1152708571114219105,
        -4356742874647865835,
        7889674025587210255,
        -15063047445053702,
        1141886611049844721,
        -7631037532535991852,
        -7982034127000075330,
        -6234520482433768610,
        -1710360246199412092,
        4546857235350971184,
        4583808669371655117,
        -5407509412797283957,
        6229851483527453949,
        -7243389174711685803,
        5818523204758407422,
        451431109683129954,
        8319638437045870110,
        6809326219677622260,
        8556296580499353143,
        3269551181474795397,
        -5974473391241630449,
        -5246761733127519293,
        -4733994914873378558,
        -1307825960948043813,
        -7565129111504795170,
        -6566813981172607238,
        2038177896599595533,
        -4157820461140106224,
        5653609452294381049,
        -2202565841775117866,
        -6817890117611987541,
        -1311679443604766958,
        3628279229051225269,
        -4525977720206120167,
        2907771609439525970,
        778059278289524373,
        6984359371816035275,
        -3936364606998129528,
        -5298787210079405285,
        2034188968277120912,
        -4387870378401475627,
        554672037112194924,
        5840819797252213829,
        -6141412821020480834,
        7866485694190557398,
        7388975574969141522,
        -4726765176532574285,
        6738701484706097438,
        -4357952859750176081,
        5952195970042072907,
        -8988751357500304535,
        8830011414065963124,
        2419637729810828715,
        586541553579625708,
        8198777404514432018,
        -5690429332067571771,
        -5182139003232109836,
        -4096094371451621809,
        5314520057811676541,
        -4033293396040907809,
        -622504768958473957,
        -64131069627898415,
        4410735263197203227,
        8212144607526460367,
        2402170585793741972,
        4325283475773974610,
        -6344159268077467517,
        -7890971429993775768,
        -8190969857040160713,
        4617151108230248888,
        -1470552416272715281,
        -5095890738279829053,
        -4113790345307396671,
        4469205987600845164,
        3241129027203716284,
        8205001152228352441,
        -3141642311557935535,
        -7343378748249317669,
        -6678700892246573074,
        5596136011188865643,
        1206361913488075499,
        -1143135553327786506,
        4914520655968408745,
        4227465294407942587,
        5607375553831556344,
        6861091380696820658,
        -2635540877404264775,
        -3558812817526879536,
        7170789582926847684,
        4892933472165288585,
        597173744976948491,
        -6091386647335781738,
        1371725377195789311,
        -7210074501640255752,
        3295738803696009778,
        546636298196020284,
        -5383611729103365571,
        5124104824591383551,
        2135919692763215075,
        -5695875964136749644,
        6438111700768233846,
        4208357349436110756,
        3091674171583948921,
        -7477735970847863754,
        -3506913595212098426,
        -8947977927743584400,
        6437570226861442418,
        979401604932372669,
        -7264508646647264850,
        -5874799961348262672,
        -7678580529857168913,
        6820729910234791048,
        -996271069855517935,
        -5784334880520928715,
        4142656736146180664,
        8201412039385703835,
        -5393578045340737736,
        549670855068890709,
        -6292301921292308170,
        5827649394587063099,
        -3398439017277695601,
        2869919216733092328,
        8363831910784389368,
        5897574581860534902,
        -8306706606536940827,
        -8812761396236860533,
        5816535479574000431,
        -5605727922123064679,
        1750179170939337200,
        -759857275904125856,
        2392129137028815281,
        435317679251669431,
        1562823400580920263,
        965148254923310220,
        8669113822474706681,
        -3326272830183554775,
        -570055038919252890,
        6456096406736068536,
};

static struct {
    jfieldID mPtr;
} gBuzhashClass;

class BuzHash {
public:
    BuzHash(int windowSize);

    uint64_t addByte(uint8_t byte);
    void reset();


    const int mWindowSize;
    uint8_t* const mWindow;
    uint64_t mHashvalue;
    int mBufPos;
    uint64_t mBytesAdded;
};

BuzHash::BuzHash(int windowSize)
 : mWindowSize(windowSize), mWindow(new uint8_t[windowSize]){
    reset();
}

uint64_t BuzHash::addByte(uint8_t b) {
    mHashvalue = rotl64(mHashvalue, 1);

    if(++mBytesAdded > mWindowSize) {
        mHashvalue ^= rotl64(HASHMAP[mWindow[mBufPos]], mWindowSize);
    }

    mHashvalue ^= HASHMAP[b];
    mWindow[mBufPos] = b;
    mBufPos = (mBufPos + 1) % mWindowSize;

    return mHashvalue;
}

void BuzHash::reset() {
    mHashvalue = 0;
    mBytesAdded = 0;
    mBufPos = 0;
}

static inline BuzHash* getNativeObj(JNIEnv* env, jobject obj) {
    return (BuzHash*)env->GetLongField(obj, gBuzhashClass.mPtr);
}

static jobject native_create(JNIEnv* env, jclass clazz, jint windowsize) {
    jobject retval = env->AllocObject(clazz);

    BuzHash* nativeObj = new BuzHash(windowsize);

    env->SetLongField(retval, gBuzhashClass.mPtr, (jlong)nativeObj);
    return retval;
}

static void native_finalize(JNIEnv* env, jobject obj) {
    BuzHash* nativeObj = getNativeObj(env, obj);
    delete nativeObj;

    env->SetLongField(obj, gBuzhashClass.mPtr, 0);
}

static jint native_addBytes(JNIEnv* env, jobject obj,
                            jbyteArray buf, jint offset, jint len, jlong matchbytes) {

    BuzHash* nativeObj = getNativeObj(env, obj);
    uint8_t* nativeBuf = (uint8_t *) env->GetByteArrayElements(buf, NULL);

    for(jint i=0;i<len;i++) {
        uint64_t hash = nativeObj->addByte(nativeBuf[offset + i]);
        if((hash & matchbytes) == 0) {
            env->ReleaseByteArrayElements(buf, (jbyte*)nativeBuf, 0);
            return i;
        }
    }

    env->ReleaseByteArrayElements(buf, (jbyte*)nativeBuf, 0);

    return -1;
}

static void native_reset(JNIEnv* env, jobject obj) {
    getNativeObj(env, obj)->reset();
}

static jlong native_hash(JNIEnv* env, jobject obj) {
    return getNativeObj(env, obj)->mHashvalue;
}


JNIEXPORT JNINativeMethod gBuzhashMethods[] = {
        {"native_finalize", "()V", (void*) native_finalize },
        {"create", "(I)Lcom/devsmart/bdiff/buzhash/NativeBuzhash64;", (void*) native_create},
        {"addBytes", "([BIIJ)I", (void*) native_addBytes},
        {"reset", "()V", (void*) native_reset},
        {"hash", "()J", (void*) native_hash}
};


static jint Buzhash_onLoad(JNIEnv* env) {
    const char* className = CLASS_NAME;
    jclass clazz = env->FindClass(className);
    if(clazz == NULL) {
        //ALOGE("can not find class %s", className);
        return -1;
    }

    gBuzhashClass.mPtr = env->GetFieldID(clazz, "mPtr", "J");

    if (env->RegisterNatives(clazz, gBuzhashMethods, NELEM(gBuzhashMethods)) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *pjvm, void *reserved) {
    //ALOGI("in JNI_OnLoad");
    JNIEnv* env = 0;
    jint retval;
    pjvm->GetEnv((void**)&env, JNI_VERSION_1_6);


    if(Buzhash_onLoad(env) != JNI_TRUE) {
        return retval;
    }

    return JNI_VERSION_1_6;
}
