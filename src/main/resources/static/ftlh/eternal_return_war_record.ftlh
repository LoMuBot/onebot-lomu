  <div class="war_record1">
        <div class="war_rank">
            <div>#${rank}</div>
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
                <img src="${traitSkillGroupUrl}"
                     alt="">
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
            <div class="rp">
                <div class="play_data_title">${kda}</div>
                <div class="play_data_label">KDA</div>
            </div>
            <div class="route">
                <div class="play_data_title">${routeId}</div>
                <div class="play_data_label">路径ID
                </div>
            </div>
        </div>

        <ul class="item_box">
            <li class="item">
                <img class="item_bg" src="${itemBg1Url}"
                     alt="">
                <img class="item_img"
                     src="${item1Url}" alt="">
            </li>
            <li class="item">
                <img class="item_bg" src="${itemBg2Url}"
                     alt="">
                <img class="item_img"
                     src="${item2Url}" alt="">
            </li>
            <li class="item">
                <img class="item_bg" src="${itemBg3Url}"
                     alt="">
                <img class="item_img"
                     src="${item3Url}" alt="">
            </li>
            <li class="item">
                <img class="item_bg" src="${itemBg4Url}"
                     alt="">
                <img class="item_img"
                     src="${item4Url}" alt="">
            </li>
            <li class="item">
                <img class="item_bg" src="${itemBg5Url}"
                     alt="">
                <img class="item_img"
                     src="${item5Url}" alt="">
            </li>
        </ul>
        <div class="game_id">Game ID
            ${gameId}
            (${version})
        </div>
    </div>