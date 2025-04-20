package cn.luorenmu.service

import action.commandProcess.eternalReturn.entity.EternalReturnCharacterById
import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.common.utils.StringLockUtils
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

/**
 * @author LoMu
 * Date 2025.04.18 12:35
 */
@Service
class ImageService(
    private val eternalReturnRequestData: EternalReturnRequestData,
) {
    fun getTierImage(id: Int) = runBlocking { eternalReturnRequestData.checkTierIconExistThenGetPathOrDownload(id) }

    fun getEternalReturnCharacterImage(type: EternalReturnCharacterById.CharacterImgUrlType, id: Int, skin: Long) =
        runBlocking {
            StringLockUtils.lock("${type.type}-$skin:$id") {
                eternalReturnRequestData.getCharacterImg(
                    id,
                    type,
                    skin
                )
            }
        }

    fun getEternalReturnItemImage(id: Long) = runBlocking {
        StringLockUtils.lock("item:$id") {
            eternalReturnRequestData.getItemIcon(id)
        }
    }


    fun getEternalReturnTacticalSkillImage(id: Long) = runBlocking {
        StringLockUtils.lock("tactical_skill:$id") {
            eternalReturnRequestData.getTacticalSkillIcon(id)
        }
    }


    fun getEternalReturnWeaponImage(id: Int) = runBlocking {
        StringLockUtils.lock("weapon:$id") {
            eternalReturnRequestData.getWeaponIcon(id)
        }
    }


    fun getEternalReturnItemBgImage(id: Int) = runBlocking {
        StringLockUtils.lock("itemBg:$id") {
            eternalReturnRequestData.getItemGradeBg(id)
        }
    }


    fun getEternalReturnTraitSkillImage(id: Long) = runBlocking {
        StringLockUtils.lock("TraitSkill:$id") {
            eternalReturnRequestData.getTraitSkillsIcon(id)
        }
    }

}