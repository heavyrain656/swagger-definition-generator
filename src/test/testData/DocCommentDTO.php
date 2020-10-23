<?php


namespace tests;


class DocCommentDTO
{
    /**
     * simple comment
     */
    public string $prop1;

    /**
     * @var string
     * @author <pavel.semenov111@gmail.com>
     * simple comment with tags around
     * @inheritdoc
     */
    public string $failedProp1;

    /**
     * simple comment with tags
     * @var string
     * @author <pavel.semenov111@gmail.com>
     * @inheritdoc
     */
    public string $prop2;

    /**
     * <p>html <em>comment</em></p>
     */
    public string $prop3;

    /**
     * simple multiline comment
     * with second line
     * and third line
     */
    public string $prop4;

    /**
     * <p>html multiline comment</p>
     * <span>with second line
     * and <b>third</b> line</span>
     */
    public string $prop5;

    /**
     * @var string
     * @author <pavel.semenov111@gmail.com>
     * @inheritdoc
     */
    public string $failedProp2;

    public string $failedProp3;
}