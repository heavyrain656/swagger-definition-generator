<?php


namespace tests;


class ClassTestDTO
{
    public const CONSTANT = 1;

    public static $staticProp;
    /**
     * @var string
     */
    public $stringProperty;
    /**
     * @var int
     */
    public $intProperty;
    /**
     * @var int[]
     */
    public $typedArrayProperty;
    /**
     * @var array
     */
    public $arrayProperty;
    /**
     * @var mixed
     */
    public $mixedProperty;

    public $undefinedProperty;

    public string $typedProperty;

    private string $privateTypedProperty;
    /**
     * @var string
     */
    protected $protectedTypedProperty;
    /**
     * @var RefTestDTO
     */
    public $objectProperty;

    public RefTestDTO $typedObjectProperty;

    public ?RefTestDTO $typedNullableObjectProperty;

    /**
     * @var RefTestDTO[]
     */
    public array $classArrayProperty;
    /**
     * @var string|int|RefTestDTO
     */
    public $multipleTypesProperty;
}