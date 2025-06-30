<div class="war_record">
    <#if rank == 99>
        <div class="war_record_left_escape"></div>
        <div class="war_record_right_escape"></div>
    <#elseif rank gte 3 || (type == "钴协议" && rank == 2) >
        <div class="war_record_left_top3"></div>
        <div class="war_record_right_top3"></div>
    <#else>
        <div class="war_record_left_top${rank}"></div>
        <div class="war_record_right_top${rank}"></div>
    </#if>
    <div class="war_record1">
        <div class="war_rank">
            <#if rank == 99>
                <div style="color:#475482">逃离</div>
            <#elseif type == "钴协议" && rank == 1>
                <div>胜利</div>
            <#elseif type == "钴协议" && rank == 2>
                <div>失败</div>
            <#elseif rank == 1>
                <div style="color: #11B288">#${rank}</div>
            <#elseif rank == 2>
                <div style="color: #207AC7">#${rank}</div>
            <#else>
                <div>#${rank}</div>
            </#if>
            <div>${type}</div>
            <div>${dateHour}</div>
            <div>${dateMonth}</div>
        </div>
        <div class="war_record_character_info">
            <div class="hero_avatar">
                <img src="${characterAvatarUrl}"
                     alt="">
            </div>
            <div class="character_name">${characterName}</div>
        </div>
        <div class="skill">
            <div class="weapon">
                <img src="${weaponUrl}"
                     alt="">
            </div>
            <div class="trait">
                <img src="${traitSkillUrl}"
                     alt="">
            </div>
            <div class="trait">
                <img src="${skillUrl}"
                     alt="">
            </div>

            <div class="trait">
                <#if type == "钴协议">
                    <img src="/images/img-placeholder-wilson-round.png"
                         alt="">
                <#else>
                    <img src="${traitSkillGroupUrl}"
                         alt="">
                </#if>
            </div>
        </div>
        <div class="play_stat">
            <div class="stat">
                ${tk} <span>/</span> ${kill} <span>/</span> ${assist}
            </div>
            <div class="label">
                TK <span>/</span> K <span>/</span> A
            </div>
        </div>
        <div class="play_data">
            <div class="damage">
                <div class="play_data_title">${dmg}</div>
                <div class="play_data_label">DMG</div>
            </div>
            <#if type == "排位">
                <div class="rp">
                    <div class="play_data_title">${rp}
                        <#if rpChange gte 0>
                            <svg xmlns="http://www.w3.org/2000/svg" width="8" height="5" viewBox="0 0 8 5" fill="none"
                                 style="transform: none;">
                                <path d="M6.75 4.75C7.17188 4.75 7.38281 4.25781 7.07812 3.95312L4.07812 0.953125C3.89062 0.765625 3.58594 0.765625 3.39844 0.953125L0.398438 3.95312C0.09375 4.25781 0.304688 4.75 0.726562 4.75H6.75Z"
                                      fill="#FF4655"></path>
                            </svg>
                            <span style="color: #FF4655">${rpChange}</span>
                        <#else>
                            <svg xmlns="http://www.w3.org/2000/svg" width="8" height="5" viewBox="0 0 8 5"
                                 fill="none" style="transform: rotate(180deg);">
                                <path
                                        d="M6.75 4.75C7.17188 4.75 7.38281 4.25781 7.07812 3.95312L4.07812 0.953125C3.89062 0.765625 3.58594 0.765625 3.39844 0.953125L0.398438 3.95312C0.09375 4.25781 0.304688 4.75 0.726562 4.75H6.75Z"
                                        fill="#5393ca"></path>
                            </svg>
                            <span style="color: #5393CA">${rpChange}</span>
                        </#if>
                    </div>
                    <div class="play_data_label">RP</div>
                </div>
            <#else>
                <div class="rp">
                    <div class="play_data_title">${kda}</div>
                    <div class="play_data_label">KDA</div>
                </div>
            </#if>
            <div class="route">
                <div class="play_data_title">${routeId}</div>
                <div class="play_data_label">路径ID
                </div>
            </div>
        </div>

        <ul class="item_box">
            <#list equips as equip>
                <li class="item">
                    <img class="item_bg" src="${equip.itemBgUrl}"
                         alt="">
                    <img class="item_img"
                         src="${equip.itemUrl}" alt="">
                </li>
            </#list>
        </ul>
        <div class="game_id">Game ID
            ${serverName}-${gameId}
            (${version})
        </div>
    </div>
    <#if teamMates??>
        <#list teamMates as teamMate>
            <div class="war_record2">
                <div class="teammate_name">
                    <div class="play_name">${teamMate.nickName}</div>
                    <div class="teammate_rp">
                        <div class="teammate_rp_img"><img
                                    src="${teamMate.rpImageUrl}" alt=""></div>
                        <span>${teamMate.rp} RP</span>
                    </div>
                </div>
                <div class="hero_avatar">
                    <img src="${teamMate.avatarUrl}"
                         alt="">
                </div>
                <div class="skill">
                    <div class="weapon">
                        <img src="${teamMate.weaponUrl}"
                             alt="">
                    </div>
                    <div class="trait">
                        <img src="${teamMate.traitSkillUrl}"
                             alt="">
                    </div>
                    <div class="trait">
                        <img src="${teamMate.skillUrl}"
                             alt="">
                    </div>

                    <div class="trait">
                        <#if type == "钴协议">
                            <img src="/images/img-placeholder-wilson-round.png"
                                 alt="">
                        <#else>
                            <img src="${teamMate.traitSkillGroupUrl}"
                                 alt="">
                        </#if>
                    </div>
                </div>
                <div class="play_stat">
                    <div class="stat">
                        ${teamMate.tk} <span>/</span> ${teamMate.kill} <span>/</span> ${teamMate.assist}
                    </div>
                    <div class="label">
                        TK <span>/</span> K <span>/</span> A
                    </div>
                </div>
                <div class="play_data">
                    <div class="damage">
                        <div class="play_data_title">${teamMate.dmg}</div>
                        <div class="play_data_label">DMG</div>
                    </div>

                </div>

                <ul class="item_box">
                    <#list teamMate.equips as teamMateEquip>
                        <li class="item">
                            <img class="item_bg" src="${teamMateEquip.itemBgUrl}"
                                 alt="">
                            <img class="item_img"
                                 src="${teamMateEquip.itemUrl}" alt="">
                        </li>
                    </#list>
                </ul>
            </div>
        </#list>
    </#if>
</div>
