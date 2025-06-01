<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <#include "eternal_return_player_style.ftlh">
</head>

<body>
<div id="content-container">
    <div id="header">
        <div class="profile-image-wrapper">
            <#if profileImageUrl??>
                <img src="${profileImageUrl}" alt=""/>
            <#else>
                <#include "eternal_return_error_img.ftlh">
            </#if>
        </div>
        <div id="top">
            <div class="level">Lv.${level}</div>
            <div class="nickname">${nickName}</div>
        </div>
        <div id="logo">由LoMu-Bot生成</div>
    </div>
    <div id="body">
        <div id="left">
            <div id="rank">
                <h4>排位(${season})</h4>
                <div id="score">
                    <div id="rp_img">
                        <img src="${data.tierImageUrl}" alt="">
                    </div>
                    <div id="rp_box">
                        <div id="rp">
                            ${data.rp}
                        </div>
                        <div id="rp_name">
                            ${data.rpName}
                        </div>
                    </div>
                </div>
                <div id="record">
                    <div>
                        <div class="record_box">
                            <h4>游戏场次</h4>
                            <h4>${data.play}</h4>
                        </div>
                        <div class="record_box">
                            <h4>平均TK</h4>
                            <h4>${data.avgTk}</h4>
                        </div>
                        <div class="record_box">
                            <h4>平均击杀</h4>
                            <h4>${data.avgKill}</h4>
                        </div>
                        <div class="record_box">
                            <h4>平均助攻</h4>
                            <h4>${data.avgAssists}</h4>
                        </div>
                        <div class="record_box">
                            <h4>平均排名</h4>
                            <h4>${data.avgRank}</h4>
                        </div>
                        <div class="record_box">
                            <h4>平均伤害</h4>
                            <h4>${data.avgDmg}</h4>
                        </div>
                        <div class="record_box">
                            <h4>TOP 1</h4>
                            <h4>${data.top1}</h4>
                        </div>
                        <div class="record_box">
                            <h4>TOP 2</h4>
                            <h4>${data.top2}</h4>
                        </div>
                        <div class="record_box">
                            <h4>TOP 3</h4>
                            <h4>${data.top3}</h4>
                        </div>
                    </div>
                </div>
            </div>
            <section>
                <table id="rank_character_stats">
                    <thead>
                    <tr>
                        <th class="character">角色</th>
                        <th class="win-rate">胜率</th>
                        <th class="get-rp">RP</th>
                        <th class="avg-rank">平均排名</th>
                        <th class="avg-dmg">平均伤害</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list characterUseStats as character>
                        <tr>
                            <td class="character">
                                <div class="image-wrapper"><img
                                            src="${character.imgUrl}"
                                            alt=""></div>
                                <div class="info">${character.characterName}
                                    <div class="plays">${character.characterPlay} 游戲</div>
                                </div>
                            </td>
                            <td class="win-rate">${character.winRate}</td>
                            <td class="get-rp">
                                <#if character.getRP gte 0>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="8" height="5" viewBox="0 0 8 5"
                                         fill="none"
                                         style="transform: none;">
                                        <path d="M6.75 4.75C7.17188 4.75 7.38281 4.25781 7.07812 3.95312L4.07812 0.953125C3.89062 0.765625 3.58594 0.765625 3.39844 0.953125L0.398438 3.95312C0.09375 4.25781 0.304688 4.75 0.726562 4.75H6.75Z"
                                              fill="#FF4655"></path>
                                    </svg>
                                    ${character.getRP}
                                <#else>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="8" height="5" viewBox="0 0 8 5"
                                         fill="none" style="transform: rotate(180deg);">
                                        <path
                                                d="M6.75 4.75C7.17188 4.75 7.38281 4.25781 7.07812 3.95312L4.07812 0.953125C3.89062 0.765625 3.58594 0.765625 3.39844 0.953125L0.398438 3.95312C0.09375 4.25781 0.304688 4.75 0.726562 4.75H6.75Z"
                                                fill="#5393ca"></path>
                                    </svg>
                                    ${character.getRP}
                                </#if>
                            </td>
                            <td class="avg-rank">${character.avgRank}</td>
                            <td class="avg-dmg">${character.avgDmg}</td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </section>
            <section>
                <table id="recent_play">
                    <thead>
                    <tr>
                        <th class="character">一起游戏的玩家 (最近20场对局)</th>
                        <th class="win-rate">胜率</th>
                        <th class="avg-rank">平均排名</th>
                    </tr>
                    </thead>
                    <tbody>
                    ${recentPlayContent?no_esc}
                    </tbody>
                </table>
            </section>
        </div>
        <div id="right">
            ${rightContent?no_esc}
        </div>
    </div>
</div>
</body>

</html>