package in.partake.model.dto.auxiliary;

/**
 * 最後に行われたParticipationStatusの変更内容を表す
 * 
 * TODO: ENROLLED, NOT_ENROLLED という名前はよくない。別の名前を考えよう。
 * ELIGIBLE, NOT_ELIGIBLE あたりか？
 * それにともない、ModificationStatus という名前も変更する必要がある。
 */
public enum ModificationStatus {
	/**　前回までは正式な参加者であると system はみなしていた　*/ 
    ENROLLED,
    
    /**　前回までは正式には参加者ではないと system はみなしていた　*/ 
    NOT_ENROLLED,
    
    /**　ユーザの意志でステータスが変更されたので、次の回にはリマインダを送信しないようにする。　*/
    CHANGED;
    
    private static ModificationStatus SAFE_VALUE = CHANGED;
    
    /**
     * str から Status を返す
     * @param str
     * @return
     */
    public static ModificationStatus safeValueOf(String str) {
    	if (str == null) { return SAFE_VALUE; }
    	if ("".equals(str)) { return SAFE_VALUE; }
    	
    	try {
    		return valueOf(str);
    	} catch (IllegalArgumentException e) {
    		return SAFE_VALUE;
    	}
    }
}
