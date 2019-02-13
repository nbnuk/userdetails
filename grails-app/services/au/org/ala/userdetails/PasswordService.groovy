package au.org.ala.userdetails

import au.org.ala.cas.encoding.BcryptPasswordEncoder
import au.org.ala.cas.encoding.LegacyPasswordEncoder
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Value

class PasswordService {

    static final String BCRYPT_ENCODER_TYPE = 'bcrypt'
    static final String LEGACY_ENCODER_TYPE = 'legacy'

    @Value('${password.encoder}')
    String passwordEncoderType = 'bcrypt'
    @Value('${bcrypt.strength}')
    Integer bcryptStrength = 10
    @Value('${encoding.algorithm}')
    String legacyAlgorithm
    @Value('${encoding.salt}')
    String legacySalt

    /**
     * Trigger a password reset
     *
     * TODO: I suggest this method throws an exception when it fails. Just returning a false
     *  value is not helpful in identifying the error from the error page the user sees. NdR.
     *
     * @param user
     * @param newPassword
     * @return
     */
    Boolean resetPassword(user,newPassword){
       //update the password
       try {
           Password.findAllByUser(user).each {
               it.delete(flush:true)
           }

           boolean isBcrypt = passwordEncoderType.equalsIgnoreCase(BCRYPT_ENCODER_TYPE)

           def encoder = isBcrypt ? new BcryptPasswordEncoder(bcryptStrength) : new LegacyPasswordEncoder(legacySalt, legacyAlgorithm, true)
           def encodedPassword = encoder.encode(newPassword)

           //reuse object if old password
           def password = new Password()
           password.user = user
           password.password = encodedPassword
           password.type = isBcrypt ? BCRYPT_ENCODER_TYPE : LEGACY_ENCODER_TYPE
           password.created = new Date().toTimestamp()
           password.status = "CURRENT"
           password.save(flush:true)
           true
       } catch(Exception e){
           log.error(e.getMessage(),e)
           false
       }
    }

    String generatePassword(user){
       //generate a new password
       def newPassword = RandomStringUtils.random(10)

       resetPassword(user, newPassword)
       newPassword
    }
}