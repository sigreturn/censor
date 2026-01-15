#![deny(improper_ctypes_definitions)]

use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;
use rustrict::{Censor, Type};

#[unsafe(no_mangle)]
pub extern "system" fn Java_me_sigreturn_censor_natives_censor_NativeLibcensor_censor<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    input: JString<'local>,
) -> jstring {
    let input: String = env
        .get_string(&input)
        .expect("couldn't get input string")
        .into();

    let (censored, _) = Censor::from_str(input.as_str())
        .with_censor_threshold(Type::MODERATE_OR_HIGHER)
        .with_censor_first_character_threshold(Type::OFFENSIVE & Type::SEVERE)
        .with_censor_replacement('*')
        .censor_and_analyze();
    let output = env
        .new_string(censored)
        .expect("couldn't construct output string");

    output.into_raw()
}
