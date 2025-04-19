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
            <img src="${skillUrl}"
                 alt="">
        </div>
        <div class="trait">
            <img src="${traitSkillUrl}"
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
            <div class="play_data_title">${rp}
                <svg xmlns="http://www.w3.org/2000/svg" width="8"
                     height="5" viewBox="0 0 8 5" fill="none" style="transform: none;">
                    <path
                            d="M6.75 4.75C7.17188 4.75 7.38281 4.25781 7.07812 3.95312L4.07812 0.953125C3.89062 0.765625 3.58594 0.765625 3.39844 0.953125L0.398438 3.95312C0.09375 4.25781 0.304688 4.75 0.726562 4.75H6.75Z"
                            fill="#FF4655"></path>
                </svg>
                <span>${rpChange}</span>
            </div>
            <div class="play_data_label">RP</div>
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
